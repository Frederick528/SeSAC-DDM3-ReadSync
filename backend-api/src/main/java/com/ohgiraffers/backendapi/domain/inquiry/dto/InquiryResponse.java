package com.ohgiraffers.backendapi.domain.inquiry.dto;

import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryResponse {

    private Long inquiryId;
    private String title;
    private String content;
    private String status;

    public static InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .inquiryId(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .status(inquiry.getStatus().name())
                .build();
    }
}
