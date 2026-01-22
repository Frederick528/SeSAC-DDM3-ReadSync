package com.ohgiraffers.backendapi.domain.chapter.service;

import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterVectorResponseDTO;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.entity.ChapterVector;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChapterVectorService {

    private final ChapterVectorRepository chapterVectorRepository;
    private final ChapterRepository chapterRepository;
    private final WebClient embeddingServerWebClient;

    @Transactional(readOnly = true)
    public float[] getVector(String s3Url) {
        return embeddingServerWebClient.post()
                .uri("/api/v1/embed-from-s3")
                .bodyValue(Map.of("s3Url", s3Url)) // {"content": "내용"} 형태로 전송
                .retrieve()
                .bodyToMono(ChapterVectorResponseDTO.class)
                .map(ChapterVectorResponseDTO::getEmbedding)
                .block(); // 결과가 올 때까지 잠시 대기
    }

    @Transactional
    public void saveOrUpdateChapterVector(Long chapterId) {
        // 1. 해당 챕터 존재 여부 및 S3 경로 확인
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        // 2. 파이썬 AI 서버에게 S3 URL을 던져 통합 벡터(float[]) 받아오기
        // 파이썬 서버가 S3에서 파일을 읽어 평균 벡터를 계산하여 돌려줍니다.
        float[] vectorResponse = getVector(chapter.getBookContentPath());

        // 3. Upsert 로직: 존재하면 업데이트, 없으면 신규 생성
        ChapterVector chapterVector = chapterVectorRepository.findById(chapterId)
                .map(existingVector -> {
                    // 이미 데이터가 있다면 기존 엔티티의 벡터값만 갱신 (Dirty Checking 활용)
                    existingVector.updateVector(vectorResponse);
                    return existingVector;
                })
                .orElseGet(() -> {
                    // 데이터가 없다면 새로운 ChapterVector 엔티티 생성
                    return ChapterVector.builder()
                            .chapter(chapter)
                            .vector(vectorResponse)
                            .build();
                });

        // 4. 최종 저장 (신규는 Insert, 기존은 Update 쿼리가 나갑니다)
        chapterVectorRepository.save(chapterVector);
    }

    // 챕터별 유사도 검색이 필요할 경우 추가 로직 구현 가능
}