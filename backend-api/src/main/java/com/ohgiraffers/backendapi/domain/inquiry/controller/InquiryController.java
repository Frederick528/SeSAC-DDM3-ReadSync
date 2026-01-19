package com.ohgiraffers.backendapi.domain.inquiry.controller;

import com.ohgiraffers.backendapi.domain.inquiry.dto.InquiryRequest;
import com.ohgiraffers.backendapi.domain.inquiry.dto.InquiryResponse;
import com.ohgiraffers.backendapi.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService service;

    @PostMapping
    public ResponseEntity<InquiryResponse> create(
            @RequestBody InquiryRequest request
    ) {
        return ResponseEntity.ok(
                InquiryResponse.from(service.create(request, 1L))
        );
    }
}
