package com.ohgiraffers.backendapi.domain.inquiry_answers.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class InquiryAnswerRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String content;
    }
}
