package com.ohgiraffers.backendapi.domain.inquiryanswer.controller;

import com.ohgiraffers.backendapi.domain.inquiryanswer.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiryanswer.dto.InquiryAnswerResponse;
import com.ohgiraffers.backendapi.domain.inquiryanswer.service.InquiryAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry/{inquiryId}/answer")
public class InquiryAnswerController {

    private final InquiryAnswerService inquiryAnswerService;

    /** 답변 등록 (관리자) */
    @PostMapping
    public ResponseEntity<InquiryAnswerResponse> create(
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest request
    ) {
        Long adminId = 1L; // TODO 관리자 JWT
        return ResponseEntity.ok(
                InquiryAnswerResponse.from(
                        inquiryAnswerService.create(inquiryId, adminId, request)
                )
        );
    }

    /** 답변 수정 (관리자) */
    @PutMapping("/{answerId}")
    public ResponseEntity<InquiryAnswerResponse> update(
            @PathVariable Long inquiryId,
            @PathVariable Long answerId,
            @RequestBody InquiryAnswerRequest request
    ) {
        Long adminId = 1L;
        return ResponseEntity.ok(
                InquiryAnswerResponse.from(
                        inquiryAnswerService.update(answerId, adminId, request)
                )
        );
    }

    /** 답변 삭제 (관리자) */
    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inquiryId,
            @PathVariable Long answerId
    ) {
        Long adminId = 1L;
        inquiryAnswerService.delete(answerId, adminId);
        return ResponseEntity.noContent().build();
    }

    /** 답변 조회 (회원/관리자) */
    @GetMapping
    public ResponseEntity<List<InquiryAnswerResponse>> findByInquiry(
            @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(
                inquiryAnswerService.findByInquiry(inquiryId)
                        .stream()
                        .map(InquiryAnswerResponse::from)
                        .toList()
        );
    }
}
