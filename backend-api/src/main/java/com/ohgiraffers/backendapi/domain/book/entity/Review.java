package com.ohgiraffers.backendapi.domain.book.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends BaseTimeEntity {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_id")
    private User userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "book_id")
    private Book bookId;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent;
    @Column(name = "is_changed", nullable = false)
    private Boolean isChanged = false;
    @Column(name = "is_spoiler", nullable = false)
    private Boolean isSpoiler = false;
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_status", nullable = false, length = 20)
    private VisibilityStatus visibilityStatus;
    @Column(name = "spoiler_report_count")
    private Integer spoilerReportCount = 0;
    @Column(name = "violation_report_count")
    private Integer violationReportCount = 0;
    @Column(name = "like_count")
    private Integer likeCount = 0;
    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;

    @Builder
    public Review(User userId, Book bookId, Integer rating, String reviewContent, Boolean isSpoiler) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.isSpoiler = (isSpoiler != null) ? isSpoiler : false;
        this.visibilityStatus = VisibilityStatus.ACTIVE;
    }

    // 리뷰 내용 수정 로직
    public void updateContent(String reviewContent, Integer rating, Boolean isSpoiler){
        this.reviewContent = reviewContent;
        this.rating = rating;
        if (isSpoiler != null) {this.isSpoiler = isSpoiler;}
        this.isChanged = true;  // 내용이 수정되었다는것을 체크
    }
}
