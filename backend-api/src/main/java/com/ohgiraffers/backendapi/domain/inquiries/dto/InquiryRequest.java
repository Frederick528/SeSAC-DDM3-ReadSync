package com.ohgiraffers.backendapi.domain.inquiries.dto;

import lombok.Getter;

public class InquiryRequest {

    @Getter
    public static class Create {
        private String title;
        private String content;
    }
}
