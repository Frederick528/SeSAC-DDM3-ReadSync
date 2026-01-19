package com.ohgiraffers.backendapi.domain.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeRequest {

    /** 공지 생성 요청 */
    @Getter
    @NoArgsConstructor
    public static class Create {
        private String title;
        private String content;
    }

    /** 공지 수정 요청 */
    @Getter
    @NoArgsConstructor
    public static class Update {
        private String title;
        private String content;
    }
}
