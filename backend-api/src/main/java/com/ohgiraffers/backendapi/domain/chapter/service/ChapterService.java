package com.ohgiraffers.backendapi.domain.chapter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterRequestDTO;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterResponseDTO;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterUrlRequestDTO;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper; // JSON 파싱용

    @Value("${file.upload-dir}")
    private String uploadDir;

    /* [1] 챕터 생성 (파일 업로드 + 메타데이터 추출) */

    @Transactional
    public ChapterResponseDTO createChapter(ChapterRequestDTO requestDTO) {
        // 1. 책 존재 여부 확인
        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        // 2. 파일 저장 (로컬 -> 추후 S3로 변경 포인트)
        String storedFilePath = saveFileToLocal(requestDTO.getFile());

        // 3. 메타데이터 결정 (수동 입력 vs 파일 자동 추출)
        Integer finalSequence = requestDTO.getSequence();
        String finalChapterName = requestDTO.getChapterName();

        // 4. 수동 입력이 하나라도 비어있으면 파일을 읽어서 추출 시도
        if (finalSequence == null || finalChapterName == null || finalChapterName.isEmpty()) {
            try {
                JsonNode rootNode = readFileToJsonNode(storedFilePath);

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

            } catch (Exception e) {
                log.warn("메타데이터 추출 실패 (기본값 진행): {}", e.getMessage());
            }
        }

        // 기본값 방어 로직
        if (finalSequence == null)
            finalSequence = 1;
        if (finalChapterName == null)
            finalChapterName = "Untitled Chapter";

        // 4. 엔티티 생성 및 저장 (빌더 패턴 사용 가정)

        Chapter chapter = Chapter.builder()
                .book(book)
                .chapterName(finalChapterName)
                .sequence(finalSequence)
                .bookContentPath(storedFilePath)
                .isEmbedded(false) // 임베딩은 이번 단계 제외
                .build();

        Chapter savedChapter = chapterRepository.save(chapter);

        // 5. 응답 생성 (생성 시에는 내용을 굳이 다 안 내려줘도 되면 content는 null 처리 가능)
        return convertToResponseDTO(savedChapter, false);
    }

    /* [1-2] 챕터 생성 (URL 기반) */
    @Transactional
    public ChapterResponseDTO createChapterByUrl(ChapterUrlRequestDTO requestDTO) {
        // 1. 책 존재 여부 확인
        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        // 2. URL 유효성 검사 (간단한 null 체크)
        if (requestDTO.getContentUrl() == null || requestDTO.getContentUrl().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "contentUrl은 필수입니다.");
        }

        // 3. 메타데이터 기본값 설정
        Integer finalSequence = requestDTO.getSequence() != null ? requestDTO.getSequence() : 1;
        String finalChapterName = requestDTO.getChapterName() != null ? requestDTO.getChapterName()
                : "Untitled Chapter";
        Integer finalParagraphs = requestDTO.getParagraphs() != null ? requestDTO.getParagraphs() : -1;

        // 4. 엔티티 생성 및 저장
        Chapter chapter = Chapter.builder()
                .book(book)
                .chapterName(finalChapterName)
                .sequence(finalSequence)
                .bookContentPath(requestDTO.getContentUrl()) // URL을 경로로 저장
                .paragraphs(finalParagraphs)
                .build();

        Chapter savedChapter = chapterRepository.save(chapter);

        // 5. 응답 생성
        return convertToResponseDTO(savedChapter, false);
    }

    /* [2] 챕터 조회 (파일 내용을 읽어서 반환) */

    public ChapterResponseDTO getChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 내용을 포함하여 DTO 반환
        return convertToResponseDTO(chapter, true);
    }

    /* [3] 챕터 수정 (파일 변경 시 is_embedded -> false) */
    @Transactional
    public ChapterResponseDTO updateChapter(Long chapterId, ChapterRequestDTO requestDTO) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 1. 파일이 수정된 경우 처리
        if (requestDTO.getFile() != null && !requestDTO.getFile().isEmpty()) {
            // 기존 파일 삭제 (로컬 스토리지 관리)
            deleteLocalFile(chapter.getBookContentPath());

            // 새 파일 저장
            String newFilePath = saveFileToLocal(requestDTO.getFile());

            // 엔티티 업데이트 (경로 변경 + isEmbedded = false 초기화)
            chapter.updateFile(newFilePath);

            // (선택) 여기서 바로 임베딩 재요청 이벤트를 발행할 수도 있음.
        }

        // 2. 메타데이터(이름, 순서) 수정
        chapter.updateMetadata(requestDTO.getChapterName(), requestDTO.getSequence());

        // 3. 변경사항 저장 (JPA Dirty Checking으로 자동 저장되지만 명시적 save도 무관)
        return convertToResponseDTO(chapterRepository.save(chapter), false);
    }

    /* [3-2] 챕터 수정 (URL 기반) */
    @Transactional
    public ChapterResponseDTO updateChapterByUrl(Long chapterId, ChapterUrlRequestDTO requestDTO) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 1. URL이 수정된 경우 처리
        if (requestDTO.getContentUrl() != null && !requestDTO.getContentUrl().isEmpty()) {
            chapter.updateUrl(requestDTO.getContentUrl());
        }

        // 2. 메타데이터(이름, 순서) 수정
        chapter.updateMetadata(requestDTO.getChapterName(), requestDTO.getSequence());

        // 3. 문단 개수 수정
        chapter.updateParagraphs(requestDTO.getParagraphs());

        // 4. 변경사항 저장
        return convertToResponseDTO(chapterRepository.save(chapter), false);
    }

    /* [4] 챕터 삭제 */
    @Transactional
    public void deleteChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 1. 로컬 파일 삭제
        deleteLocalFile(chapter.getBookContentPath());

        // 2. DB 데이터 삭제
        chapterRepository.delete(chapter);
    }

    /* ------------- 내부 헬퍼 메서드 ------------- */

    // 파일 로컬 저장 로직 (추후 AWS S3 Service로 대체될 부분)
    private String saveFileToLocal(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

        try {
            // 1. 상대 경로(./uploads...)를 절대 경로로 변환하여 명확하게 처리
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            File directory = uploadPath.toFile();

            // 2. 디렉토리가 없으면 생성 (프로젝트 폴더 내에 uploads/chapters 폴더가 생김)
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    log.info("디렉토리가 생성되었습니다: {}", directory.getAbsolutePath());
                }
            }

            // 3. 유니크한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String storeFileName = UUID.randomUUID() + "_" + originalFilename;

            // 4. 저장 경로 결합
            Path targetPath = uploadPath.resolve(storeFileName);

            // 5. 파일 저장
            file.transferTo(targetPath.toFile());

            log.info("파일이 저장되었습니다: {}", targetPath);

            // DB에 저장할 경로 반환 (나중에 읽을 때 사용)
            return targetPath.toString();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    // 저장된 파일 경로에서 JSON Node 읽기
    private JsonNode readFileToJsonNode(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND, "Path: " + filePath);
            }
            return objectMapper.readTree(path.toFile());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_READ_ERROR, e.getMessage());
        }
    }

    // 엔티티 -> Response DTO 변환
    private ChapterResponseDTO convertToResponseDTO(Chapter chapter, boolean includeContent) {
        Object content = null;
        if (includeContent) {
            try {
                // 저장된 경로에서 파일을 읽어 Object(Map/List) 형태로 변환
                content = readFileToJsonNode(chapter.getBookContentPath());
            } catch (CustomException e) {
                log.error("컨텐츠 로드 실패: {}", e.getMessage());
                content = "Error: 내용을 불러올 수 없습니다.";
            }
        }
        return ChapterResponseDTO.builder()
                .chapterId(chapter.getChapterId())
                .bookId(chapter.getBook().getBookId())
                .chapterName(chapter.getChapterName())
                .sequence(chapter.getSequence())
                .bookContentPath(chapter.getBookContentPath())
                .bookContent(content)
                .paragraphs(chapter.getParagraphs())
                .build();
    }

    // LocalFile 삭제 메서드
    private void deleteLocalFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                // 파일 삭제 실패가 DB 트랜잭션을 롤백시키지 않도록 로그만 남김 (Orphan file 정책)
                log.warn("파일 삭제 실패: {}", filePath, e);
            }
        }
    }
}