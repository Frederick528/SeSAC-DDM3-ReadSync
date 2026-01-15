package com.ohgiraffers.backendapi.domain.inquiries.dto;

import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class InquiryResponse {

    @Getter
    @Builder
    public static class Detail {
        private Long inquiryId;
        private String title;
        private String content;
        private String status;
        private LocalDateTime createdAt;

        public static Detail from(Inquiry inquiry) {
            return Detail.builder()
                    .inquiryId(inquiry.getInquiryId())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .status(inquiry.getStatus().name())
                    .createdAt(inquiry.getCreatedAt())
                    .build();
        }
    }
}
