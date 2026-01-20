package com.ohgiraffers.backendapi.domain.community_comment.dto;

import com.ohgiraffers.backendapi.domain.community_comment.entity.CommunityComment;
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
                .userId(comment.getUser().getId())   // ✅ 여기 핵심
                .postId(comment.getPost().getPostId())
                .parentId(
                        comment.getParent() != null
                                ? comment.getParent().getCommentId()
                                : null
                )
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
