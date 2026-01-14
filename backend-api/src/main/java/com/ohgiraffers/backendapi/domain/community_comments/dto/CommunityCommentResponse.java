package com.ohgiraffers.backendapi.domain.community_comments.dto;

import com.ohgiraffers.backendapi.domain.community_comments.entity.CommunityComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityCommentResponse {

    private Long commentId;
    private String content;
    private Long userId;
    private Long parentId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommunityCommentResponse from(CommunityComment comment) {
        return CommunityCommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .parentId(comment.getParentId())
                .postId(comment.getPostId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
