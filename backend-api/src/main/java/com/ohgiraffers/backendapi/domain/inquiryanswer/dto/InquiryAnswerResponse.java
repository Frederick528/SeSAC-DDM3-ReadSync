package com.ohgiraffers.backendapi.domain.inquiryanswer.dto;

import com.ohgiraffers.backendapi.domain.inquiryanswer.entity.InquiryAnswer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryAnswerResponse {

    private Long answerId;
    private Long inquiryId;
    private String content;

    public static InquiryAnswerResponse from(InquiryAnswer answer) {
        return InquiryAnswerResponse.builder()
                .answerId(answer.getId())
                .inquiryId(answer.getInquiry().getId())
                .content(answer.getContent())
                .build();
    }
}
