package com.ohgiraffers.backendapi.domain.booklog.controller;

import com.ohgiraffers.backendapi.domain.booklog.dto.BookLogRequestDTO;
import com.ohgiraffers.backendapi.domain.booklog.dto.BookLogResponseDTO;
import com.ohgiraffers.backendapi.domain.booklog.service.BookLogService;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/book-logs")
@RequiredArgsConstructor
public class BookLogController {

    private final BookLogService bookLogService;

    @GetMapping("user/{userId}")
    public ResponseEntity<List<BookLogResponseDTO>> getLogByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookLogService.findAllByUser(userId));
    }

    @GetMapping("me")
    public ResponseEntity<List<BookLogResponseDTO>> getMyBookLog(@CurrentUserId Long userId) {

        return ResponseEntity.ok(bookLogService.findAllByUser(userId));
    }

    /**
     * 독서 기록 저장 또는 누적 업데이트
     * POST /api/v1/book-logs
     */
    @PostMapping
    public ResponseEntity<BookLogResponseDTO> saveOrUpdate(@RequestBody BookLogRequestDTO requestDTO) {
        BookLogResponseDTO response = bookLogService.saveOrUpdate(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 독서 기록 상세 조회
     * GET /api/v1/book-logs/{bookLogId}
     */
    @GetMapping("book/{bookLogId}")
    public ResponseEntity<BookLogResponseDTO> getLog(@PathVariable Long bookLogId) {
        return ResponseEntity.ok(bookLogService.getLog(bookLogId));
    }

    /**
     * 독서 기록 삭제
     * DELETE /api/v1/book-logs/{bookLogId}
     */
    @DeleteMapping("/{bookLogId}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long bookLogId) {
        bookLogService.deleteLog(bookLogId);
        return ResponseEntity.noContent().build();
    }
}