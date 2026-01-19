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
    private Comment comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 10)
    private LikeType likeType;

    @Builder
    public Like(User user, Comment comment, Review review, LikeType likeType) {
        this.user = user;
        this.comment = comment;
        this.review = review;
        this.likeType = likeType;
    }

    public void updateLikeType(LikeType newType) {
        this.likeType = newType;
    }

// ※ builderMethodName으로 사용시 작동 안함.
//    // 1. 댓글 좋아요 빌더(Service의 Helper Method의 saveCommentLike에서 사용함)
//    @Builder(builderMethodName = "createCommentLike")
//    public Like(Comment comment, User user, LikeType likeType) {
//        this.comment = comment;
//        this.user = user;
//        this.likeType = likeType;
//    }
//
//    // 2. 리뷰 좋아요 빌더(위와 동일)
//    @Builder(builderMethodName = "createReviewLike")
//    public Like(Review review, User user, LikeType likeType) {
//        this.review = review;
//        this.user = user;
//        this.likeType = likeType;
//    }
}
