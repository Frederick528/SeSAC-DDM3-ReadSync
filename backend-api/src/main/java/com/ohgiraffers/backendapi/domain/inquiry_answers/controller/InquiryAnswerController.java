package com.ohgiraffers.backendapi.domain.inquiry_answers.controller;

import com.ohgiraffers.backendapi.domain.inquiry_answers.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiry_answers.dto.InquiryAnswerResponse;
import com.ohgiraffers.backendapi.domain.inquiry_answers.service.InquiryAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries/{inquiryId}/answers")
public class InquiryAnswerController {

    private final InquiryAnswerService service;

    @PostMapping
    public ResponseEntity<InquiryAnswerResponse> create(
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest.Create request
    ) {
        return ResponseEntity.ok(
                InquiryAnswerResponse.from(
                        service.create(inquiryId, 1L, request)
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<InquiryAnswerResponse>> findAll(
            @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(
                service.findByInquiry(inquiryId).stream()
                        .map(InquiryAnswerResponse::from)
                        .toList()
        );
    }
}
