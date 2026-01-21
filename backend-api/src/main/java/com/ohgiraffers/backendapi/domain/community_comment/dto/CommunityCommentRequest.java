package com.ohgiraffers.backendapi.domain.community_comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommunityCommentRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String content;
        private Long parentId;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String content;
    }
}
