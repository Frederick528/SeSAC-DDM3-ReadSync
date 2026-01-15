package com.ohgiraffers.backendapi.domain.inquiry_answers.dto;

import com.ohgiraffers.backendapi.domain.inquiry_answers.entity.InquiryAnswer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryAnswerResponse {

    private Long answerId;
    private String content;
    private Long inquiryId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InquiryAnswerResponse from(InquiryAnswer answer) {
        return InquiryAnswerResponse.builder()
                .answerId(answer.getAnswerId())
                .content(answer.getContent())
                .inquiryId(answer.getInquiry().getInquiryId())
                .userId(answer.getUser().getId())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}
