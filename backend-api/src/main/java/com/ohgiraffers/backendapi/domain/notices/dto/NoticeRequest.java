package com.ohgiraffers.backendapi.domain.notices.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String title;
        private String content;
    }
}
