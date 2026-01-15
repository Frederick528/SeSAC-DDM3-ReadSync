package com.ohgiraffers.backendapi.domain.inquiry_answers.dto;

import lombok.Getter;

public class InquiryAnswerRequest {

    @Getter
    public static class Create {
        private String content;
    }
}
