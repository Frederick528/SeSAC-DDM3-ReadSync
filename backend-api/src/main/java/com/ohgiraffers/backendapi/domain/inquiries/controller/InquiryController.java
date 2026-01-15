package com.ohgiraffers.backendapi.domain.inquiries.controller;

import com.ohgiraffers.backendapi.domain.inquiries.dto.InquiryRequest;
import com.ohgiraffers.backendapi.domain.inquiries.dto.InquiryResponse;
import com.ohgiraffers.backendapi.domain.inquiries.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService service;

    @PostMapping
    public ResponseEntity<InquiryResponse> create(
            @RequestBody InquiryRequest.Create request
    ) {
        return ResponseEntity.ok(
                InquiryResponse.from(service.create(request, 1L))
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<InquiryResponse>> myInquiries() {
        return ResponseEntity.ok(
                service.findMyInquiries(1L).stream()
                        .map(InquiryResponse::from)
                        .toList()
        );
    }
}
