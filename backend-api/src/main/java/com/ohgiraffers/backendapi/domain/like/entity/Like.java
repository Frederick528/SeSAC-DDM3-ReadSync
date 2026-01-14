package com.ohgiraffers.backendapi.domain.like.entity;

import com.ohgiraffers.backendapi.domain.review.entity.Review;
import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.domain.like.enums.LikeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes")
public class Like extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review reviewId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 10)
    private LikeType likeType;

    // 1. 댓글 좋아요 빌더
    @Builder(builderMethodName = "createCommentLike")
    public Like(Comment commentId, User userId, LikeType likeType) {
        this.commentId = commentId;
        this.userId = userId;
        this.likeType = likeType;
    }

    // 2. 리뷰 좋아요 빌더
    @Builder(builderMethodName = "createReviewLike")
    public Like(Review reviewId, User userId, LikeType likeType) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.likeType = likeType;
    }
}
