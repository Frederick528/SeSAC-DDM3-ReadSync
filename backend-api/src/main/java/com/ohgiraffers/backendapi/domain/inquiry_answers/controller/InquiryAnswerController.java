package com.ohgiraffers.backendapi.domain.inquiry_answer.controller;

import com.ohgiraffers.backendapi.domain.inquiry_answer.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiry_answer.dto.InquiryAnswerResponse;
import com.ohgiraffers.backendapi.domain.inquiry_answer.service.InquiryAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries/{inquiryId}/answer")
public class InquiryAnswerController {

    private final InquiryAnswerService service;

    @PostMapping
    public ResponseEntity<InquiryAnswerResponse> create(
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest request
    ) {
        return ResponseEntity.ok(
                InquiryAnswerResponse.from(
                        service.create(inquiryId, 1L, request)
                )
        );
    }
}

