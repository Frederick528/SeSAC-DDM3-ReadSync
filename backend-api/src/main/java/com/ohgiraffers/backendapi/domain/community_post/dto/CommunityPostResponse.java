package com.ohgiraffers.backendapi.domain.community_post.dto;

import com.ohgiraffers.backendapi.domain.community_post.entity.CommunityPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityPostResponse {

    private Long postId;
    private String title;
    private String content;
    private int views;
    private int likeCount;
    private int report;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommunityPostResponse from(CommunityPost post) {
        return CommunityPostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .report(post.getReport())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
