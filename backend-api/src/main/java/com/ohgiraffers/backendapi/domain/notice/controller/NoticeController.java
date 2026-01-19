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

    /** 공지 생성 (ADMIN) */
    @PostMapping
    public ResponseEntity<NoticeResponse> create(
            @RequestBody NoticeRequest.Create request
    ) {
        Long adminId = 1L; // 임시 관리자 ID

        return ResponseEntity.ok(
                NoticeResponse.from(
                        noticeService.create(request, adminId)
                )
        );
    }

    /** 공지 전체 조회 (회원/비회원) */
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> findAll() {
        return ResponseEntity.ok(
                noticeService.findAll()
                        .stream()
                        .map(NoticeResponse::from)
                        .toList()
        );
    }

    /** 공지 상세 조회 */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> find(@PathVariable Long noticeId) {
        return ResponseEntity.ok(
                NoticeResponse.from(
                        noticeService.find(noticeId)
                )
        );
    }
}
