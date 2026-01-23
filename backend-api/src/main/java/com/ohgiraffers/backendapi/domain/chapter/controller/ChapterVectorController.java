package com.ohgiraffers.backendapi.domain.chapter.controller;

import com.ohgiraffers.backendapi.domain.chapter.service.ChapterVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor

public class ChapterVectorController {

    private final ChapterVectorService chapterVectorService;

    /**
     * 특정 챕터의 S3 파일을 읽어 벡터를 생성하거나 업데이트합니다.
     * POST /api/chapters/{chapterId}/embed
     */
    @PostMapping("/{chapterId}/embed")
    public ResponseEntity<String> createOrUpdateVector(@PathVariable Long chapterId) {
        try {
            // 이전에 만든 통합 서비스를 호출합니다.
            // S3 URL 조회 -> 파이썬 호출 -> 통합 벡터 생성 -> DB 저장을 한 번에 수행합니다.
            chapterVectorService.saveOrUpdateChapterVector(chapterId);

            return ResponseEntity.accepted()
                    .body("벡터 생성 작업이 백그라운드에서 시작되었습니다. (Chapter ID: " + chapterId + ")");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("벡터 생성 중 오류 발생: " + e.getMessage());
        }
    }
}