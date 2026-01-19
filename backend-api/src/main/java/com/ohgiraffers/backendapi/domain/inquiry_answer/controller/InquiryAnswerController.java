package com.ohgiraffers.backendapi.domain.inquiry_answer.controller;

import com.ohgiraffers.backendapi.domain.inquiry_answer.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiry_answer.dto.InquiryAnswerResponse;
import com.ohgiraffers.backendapi.domain.inquiry_answer.service.InquiryAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiry/{inquiryId}/answer")
public class InquiryAnswerController {

    private final InquiryAnswerService service;

    /** 관리자 답변 작성 */
    @PostMapping
    public ResponseEntity<InquiryAnswerResponse> create(
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest request
    ) {
        Long adminId = 1L;
        return ResponseEntity.ok(
                InquiryAnswerResponse.from(
                        service.create(inquiryId, adminId, request)
                )
        );
    }

    /** 답변 조회 (회원/비회원) */
    @GetMapping
    public ResponseEntity<List<InquiryAnswerResponse>> find(
            @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(
                service.findByInquiry(inquiryId)
                        .stream()
                        .map(InquiryAnswerResponse::from)
                        .toList()
        );
    }
}
