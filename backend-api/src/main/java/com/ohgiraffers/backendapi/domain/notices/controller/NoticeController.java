package com.ohgiraffers.backendapi.domain.notices.controller;

import com.ohgiraffers.backendapi.domain.notices.dto.NoticeRequest;
import com.ohgiraffers.backendapi.domain.notices.dto.NoticeResponse;
import com.ohgiraffers.backendapi.domain.notices.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService service;

    /** 공지 목록 */
    @GetMapping
    public ResponseEntity<List<NoticeResponse.Detail>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(NoticeResponse.Detail::from)
                        .toList()
        );
    }

    /** 공지 상세 */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse.Detail> find(
            @PathVariable Long noticeId
    ) {
        return ResponseEntity.ok(
                NoticeResponse.Detail.from(service.find(noticeId))
        );
    }

    /** 공지 작성 */
    @PostMapping
    public ResponseEntity<NoticeResponse.Detail> create(
            @RequestBody NoticeRequest.Create request
    ) {
        return ResponseEntity.ok(
                NoticeResponse.Detail.from(service.create(request, 1L))
        );
    }

    /** 공지 수정 */
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse.Detail> update(
            @PathVariable Long noticeId,
            @RequestBody NoticeRequest.Update request
    ) {
        return ResponseEntity.ok(
                NoticeResponse.Detail.from(service.update(noticeId, request))
        );
    }

    /** 공지 삭제 */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long noticeId
    ) {
        service.delete(noticeId);
        return ResponseEntity.noContent().build();
    }
}
