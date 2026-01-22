package com.ohgiraffers.backendapi.domain.chapter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterRequestDTO;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterResponseDTO;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import com.ohgiraffers.backendapi.global.s3.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;
    private final S3StorageService s3StorageService;

    /**
     * [1] 챕터 생성 (S3 파일 업로드 + paragraph 카운팅 + 메타데이터 추출)
     */
    @Transactional
    public ChapterResponseDTO createChapter(ChapterRequestDTO requestDTO) {
        // 1. 책 존재 여부 확인
        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        MultipartFile file = requestDTO.getFile();

        // 2. paragraph 개수 카운팅
        int paragraphCount = s3StorageService.countParagraphsFromFile(file);
        log.info("Paragraph 카운트 완료: {}", paragraphCount);

        // 3. 메타데이터 결정 (수동 입력 vs 파일 자동 추출)
        Integer finalSequence = requestDTO.getSequence();
        String finalChapterName = requestDTO.getChapterName();

        // 4. 수동 입력이 하나라도 비어있으면 파일을 읽어서 추출 시도
        if (finalSequence == null || finalChapterName == null || finalChapterName.isEmpty()) {
            try {
                JsonNode rootNode = objectMapper.readTree(file.getInputStream());

                // 순서 자동 추출
                if (finalSequence == null && rootNode.has("chapter")) {
                    finalSequence = rootNode.get("chapter").asInt();
                }

                // 이름 자동 추출 (예: 'book_name' + 'chapter')
                if ((finalChapterName == null || finalChapterName.isEmpty()) && rootNode.has("book_name")) {
                    String bookName = rootNode.get("book_name").asText();
                    int chapterNum = rootNode.has("chapter") ? rootNode.get("chapter").asInt() : 0;
                    finalChapterName = bookName + " Chapter " + chapterNum;
                }

            } catch (IOException e) {
                log.warn("메타데이터 추출 실패 (기본값 진행): {}", e.getMessage());
            }
        }

        // 기본값 방어 로직
        if (finalSequence == null)
            finalSequence = 1;
        if (finalChapterName == null)
            finalChapterName = "Untitled Chapter";

        // 5. S3에 파일 업로드
        String s3Url = s3StorageService.uploadFile(file);
        log.info("S3 업로드 완료: {}", s3Url);

        // 6. 엔티티 생성 및 저장
        Chapter chapter = Chapter.builder()
                .book(book)
                .chapterName(finalChapterName)
                .sequence(finalSequence)
                .bookContentPath(s3Url)
                .paragraphs(paragraphCount)
                .build();

        Chapter savedChapter = chapterRepository.save(chapter);

        // 7. 응답 생성
        return convertToResponseDTO(savedChapter);
    }

    /**
     * [2] 챕터 조회 (S3 URL만 반환)
     */
    public ChapterResponseDTO getChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        return convertToResponseDTO(chapter);
    }

    /**
     * [3] 챕터 수정 (파일 변경 시 S3 재업로드 + paragraphs 재계산)
     */
    @Transactional
    public ChapterResponseDTO updateChapter(Long chapterId, ChapterRequestDTO requestDTO) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 1. 파일이 수정된 경우 처리
        if (requestDTO.getFile() != null && !requestDTO.getFile().isEmpty()) {
            MultipartFile newFile = requestDTO.getFile();

            // 기존 S3 파일 삭제
            s3StorageService.deleteFile(chapter.getBookContentPath());

            // 새 파일에서 paragraph 개수 카운팅
            int newParagraphCount = s3StorageService.countParagraphsFromFile(newFile);

            // 새 파일 S3 업로드
            String newS3Url = s3StorageService.uploadFile(newFile);

            // 엔티티 업데이트 (경로 변경 + paragraphs 갱신 + isEmbedded 초기화)
            chapter.updateFile(newS3Url, newParagraphCount);

            log.info("챕터 파일 수정 완료 - S3 URL: {}, paragraphs: {}", newS3Url, newParagraphCount);
        }

        // 2. 메타데이터(이름, 순서) 수정
        chapter.updateMetadata(requestDTO.getChapterName(), requestDTO.getSequence());

        // 3. 변경사항 저장
        return convertToResponseDTO(chapterRepository.save(chapter));
    }

    /**
     * [4] 챕터 삭제
     */
    @Transactional
    public void deleteChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 1. S3 파일 삭제
        s3StorageService.deleteFile(chapter.getBookContentPath());

        // 2. DB 데이터 삭제
        chapterRepository.delete(chapter);

        log.info("챕터 삭제 완료: {}", chapterId);
    }

    /* ------------- 내부 헬퍼 메서드 ------------- */

    /**
     * 엔티티 -> Response DTO 변환
     */
    private ChapterResponseDTO convertToResponseDTO(Chapter chapter) {
        return ChapterResponseDTO.builder()
                .chapterId(chapter.getChapterId())
                .bookId(chapter.getBook().getBookId())
                .chapterName(chapter.getChapterName())
                .sequence(chapter.getSequence())
                .paragraphs(chapter.getParagraphs())
                .contentUrl(chapter.getBookContentPath())
                .isEmbedded(chapter.getIsEmbedded())
                .build();
    }
}