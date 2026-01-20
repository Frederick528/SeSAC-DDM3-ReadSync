package com.ohgiraffers.backendapi.domain.community_post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommunityPostRequest {

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
