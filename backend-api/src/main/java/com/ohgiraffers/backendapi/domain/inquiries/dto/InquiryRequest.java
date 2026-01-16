package com.ohgiraffers.backendapi.domain.inquiries.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class InquiryRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String title;
        private String content;
    }
}
