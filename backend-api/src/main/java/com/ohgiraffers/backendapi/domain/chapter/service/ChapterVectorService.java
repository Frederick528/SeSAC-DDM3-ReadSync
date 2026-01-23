package com.ohgiraffers.backendapi.domain.chapter.service;

import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterVectorResponseDTO;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.entity.ChapterVector;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChapterVectorService {

    private final ChapterVectorRepository chapterVectorRepository;
    private final ChapterRepository chapterRepository;
    private final WebClient embeddingServerWebClient;

    @Transactional(readOnly = true)
    public List<float[]> getChapterVectorsForBook(Long bookId) {
        // 도서에 속한 모든 챕터 벡터를 가져와서 북 벡터 서비스에 전달
        return chapterVectorRepository.findAllVectorsByBookId(bookId);
    }

    public float[] getVectorS3(String s3Url) {
        return embeddingServerWebClient.post()
                .uri("/api/v1/embed-from-s3")
                .bodyValue(Map.of("s3Url", s3Url)) // {"content": "내용"} 형태로 전송
                .retrieve()
                .bodyToMono(ChapterVectorResponseDTO.class)
                .map(ChapterVectorResponseDTO::getEmbedding)
                .block(Duration.ofSeconds(1000)); // 결과가 올 때까지 잠시 대기
    }
    public float[] getVectorGD(String googleDriveUrl) {
        return embeddingServerWebClient.post()
                .uri("/api/v1/embed-from-drive")
                .bodyValue(Map.of("google_drive_url", googleDriveUrl)) // {"content": "내용"} 형태로 전송
                .retrieve()
                .bodyToMono(ChapterVectorResponseDTO.class)
                .map(ChapterVectorResponseDTO::getEmbedding)
                .block(Duration.ofSeconds(1000)); // 결과가 올 때까지 잠시 대기
    }

    @Async
    @Transactional
    public void saveOrUpdateChapterVector(Long chapterId) {

        try {
            // 1. 챕터 조회 (DB 작업)
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

            // 2. 파이썬 서버 호출 (긴 시간 소요)
            // 주의: 이 시점에도 트랜잭션이 열려 있어 DB 커넥션을 잡고 있긴 합니다.
            // (더 고도화하려면 이 부분을 트랜잭션 밖으로 빼야 하지만, 현 단계에선 이 방식도 무방합니다)
            float[] vectorResponse = getVectorGD(chapter.getBookContentPath());


            // 3. Upsert 로직 (DB 작업)
            ChapterVector chapterVector = chapterVectorRepository.findById(chapterId)
                    .map(existingVector -> {
                        existingVector.updateVector(vectorResponse);
                        return existingVector;
                    })
                    .orElseGet(() -> ChapterVector.builder()
                            .chapter(chapter)
                            .vector(vectorResponse)
                            .build());

            // 4. 최종 저장
            chapterVectorRepository.save(chapterVector);


        } catch (Exception e) {
            log.error("비동기 작업 중 실패 - ChapterId: {}, 이유: {}", chapterId, e.getMessage());
            // 필요하다면 여기서 '실패 상태'를 DB에 기록하는 로직을 추가할 수 있습니다.
        }
    }

    // 챕터별 유사도 검색이 필요할 경우 추가 로직 구현 가능
}