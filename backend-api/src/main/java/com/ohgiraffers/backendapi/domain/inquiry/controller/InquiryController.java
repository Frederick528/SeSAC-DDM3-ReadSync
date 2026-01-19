package com.ohgiraffers.backendapi.domain.inquiry.controller;

import com.ohgiraffers.backendapi.domain.inquiry.dto.InquiryRequest;
import com.ohgiraffers.backendapi.domain.inquiry.dto.InquiryResponse;
import com.ohgiraffers.backendapi.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    /** 문의 등록 (회원) */
    @PostMapping
    public ResponseEntity<InquiryResponse> create(
            @RequestBody InquiryRequest request
    ) {
        Long userId = 1L; // TODO JWT 연동
        return ResponseEntity.ok(
                InquiryResponse.from(inquiryService.create(request, userId))
        );
    }

    /** 내 문의 목록 조회 */
    @GetMapping
    public ResponseEntity<List<InquiryResponse>> findMyInquiries() {
        Long userId = 1L;
        return ResponseEntity.ok(
                inquiryService.findByUser(userId)
                        .stream()
                        .map(InquiryResponse::from)
                        .toList()
        );
    }

    /** 문의 상세 */
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponse> findOne(
            @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(
                InquiryResponse.from(inquiryService.find(inquiryId))
        );
    }
}
