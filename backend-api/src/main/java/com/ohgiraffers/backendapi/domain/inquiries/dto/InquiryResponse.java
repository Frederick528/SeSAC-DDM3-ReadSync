package com.ohgiraffers.backendapi.domain.inquiries.dto;

import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryResponse {

    private Long inquiryId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;

    public static InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .inquiryId(inquiry.getInquiryId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .status(inquiry.getStatus().name())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
