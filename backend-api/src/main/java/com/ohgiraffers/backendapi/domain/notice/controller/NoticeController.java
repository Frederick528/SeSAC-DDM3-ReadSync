package com.ohgiraffers.backendapi.domain.notice.controller;

import com.ohgiraffers.backendapi.domain.notice.dto.NoticeRequest;
import com.ohgiraffers.backendapi.domain.notice.dto.NoticeResponse;
import com.ohgiraffers.backendapi.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /* =========================
       관리자 전용
       ========================= */

    /** 공지 등록 (관리자) */
    @PostMapping
    public ResponseEntity<NoticeResponse> create(
            @RequestBody NoticeRequest request
    ) {
        Long adminId = 1L; // TODO 관리자 JWT 연동
        return ResponseEntity.ok(
                NoticeResponse.from(noticeService.create(request, adminId))
        );
    }

    /** 공지 수정 (관리자) */
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> update(
            @PathVariable Long noticeId,
            @RequestBody NoticeRequest request
    ) {
        return ResponseEntity.ok(
                NoticeResponse.from(noticeService.update(noticeId, request))
        );
    }

    /** 공지 삭제 (관리자) */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long noticeId
    ) {
        noticeService.delete(noticeId);
        return ResponseEntity.noContent().build();
    }

    /* =========================
       회원 / 공통
       ========================= */

    /** 공지 목록 */
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> findAll() {
        return ResponseEntity.ok(
                noticeService.findAll()
                        .stream()
                        .map(NoticeResponse::from)
                        .toList()
        );
    }

    /** 공지 상세 */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> findOne(
            @PathVariable Long noticeId
    ) {
        return ResponseEntity.ok(
                NoticeResponse.from(noticeService.find(noticeId))
        );
    }
}
