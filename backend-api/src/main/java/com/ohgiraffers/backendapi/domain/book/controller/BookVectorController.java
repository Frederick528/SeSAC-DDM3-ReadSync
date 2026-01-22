package com.ohgiraffers.backendapi.domain.book.controller;

import com.ohgiraffers.backendapi.domain.book.dto.BookVectorDTO;
import com.ohgiraffers.backendapi.domain.book.service.BookVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/book-vectors")
@RequiredArgsConstructor
public class BookVectorController {

    private final BookVectorService bookVectorService;

    /**
     * [관리자/시스템] 챕터 벡터들을 기반으로 특정 도서의 북 벡터를 생성 또는 업데이트합니다.
     * @param bookId 대상 도서 ID
     */
    @PostMapping("/{bookId}/generate")
    public ResponseEntity<String> generateBookVector(@PathVariable Long bookId) {
        bookVectorService.createBookVectorFromChapters(bookId);
        return ResponseEntity.ok("도서 ID " + bookId + "의 벡터가 성공적으로 생성되었습니다.");
    }

    /**
     * [사용자] 특정 도서와 가장 유사한 추천 도서 목록을 가져옵니다.
     * @param bookId 기준 도서 ID
     * @param limit 추천 개수 (기본값 5)
     */
    @GetMapping("/recommend/{bookId}")
    public ResponseEntity<List<BookVectorDTO>> getSimilarBooks(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "5") int limit) {

        List<BookVectorDTO> recommendations = bookVectorService.getRecommendationsByBookId(bookId, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * [사용자] 특정 취향 벡터(문자열 형태)를 기반으로 도서를 추천받습니다.
     * @param vector "[0.1, 0.2, ...]" 형태의 벡터 문자열
     * @param limit 추천 개수
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookVectorDTO>> searchByVector(
            @RequestParam String vector,
            @RequestParam(defaultValue = "5") int limit) {

        List<BookVectorDTO> results = bookVectorService.getRecommendationsByVector(vector, limit);
        return ResponseEntity.ok(results);
    }
}