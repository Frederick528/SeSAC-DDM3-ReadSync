package com.ohgiraffers.backendapi.domain.notices.dto;

import lombok.Getter;

public class NoticeRequest {

    @Getter
    public static class Create {
        private String title;
        private String content;
    }

    @Getter
    public static class Update {
        private String title;
        private String content;
    }
}
