package com.ohgiraffers.backendapi.domain.review.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportReasonType;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(name = "rating", nullable = false)
    private Integer rating;
    @Column(name = "review_content", columnDefinition = "TEXT", nullable = false)
    private String reviewContent;
    @Column(name = "is_changed", nullable = false)
    private Boolean isChanged = false;
    @Column(name = "is_spoiler", nullable = false)
    private Boolean isSpoiler = false;
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_status", nullable = false, length = 20)
    private VisibilityStatus visibilityStatus;
    @Column(name = "spoiler_report_count", nullable = false)
    private Integer spoilerReportCount = 0;
    @Column(name = "violation_report_count", nullable = false)
    private Integer violationReportCount = 0;
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    @Column(name = "dislike_count", nullable = false)
    private Integer dislikeCount = 0;

    @Builder
    public Review(User user, Book book, Integer rating, String reviewContent, Boolean isSpoiler) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.isSpoiler = (isSpoiler != null) ? isSpoiler : false;
        this.visibilityStatus = VisibilityStatus.ACTIVE;
    }

    // 리뷰 내용 수정 로직
    // The existing spoilerReportCount and violationReportCount fields are already
    // defined above.
    // The instruction seems to imply adding them again, but they are already there.
    // Assuming the intent was to add the methods and potentially modify the
    // existing fields if they weren't there.
    // Since they are, we'll just add the methods.

    // New update method (assuming reviewTitle is a typo and should be reviewContent
    // or removed)
    // Based on the original code, there is no 'reviewTitle' field.
    // Assuming the instruction meant to update reviewContent and isSpoiler.
    public void update(String reviewContent, boolean isSpoiler) {
        this.reviewContent = reviewContent;
        this.isSpoiler = isSpoiler;
        this.isChanged = true; // Mark as changed
    }

    public void incrementReportCount(ContentReportReasonType reasonType) { // ContentReportReasonType needs to be
                                                                           // imported or defined
        if (reasonType == ContentReportReasonType.SPOILER) {
            this.spoilerReportCount++;
            if (this.spoilerReportCount >= 5) {
                this.visibilityStatus = VisibilityStatus.BLINDED;
            }
        } else if (reasonType == ContentReportReasonType.ABUSE || reasonType == ContentReportReasonType.ADVERTISEMENT) {
            this.violationReportCount++;
            if (this.violationReportCount >= 5) {
                this.visibilityStatus = VisibilityStatus.SUSPENDED;
            }
        }
    }

    public void updateContent(String reviewContent, Integer rating, Boolean isSpoiler) {
        this.reviewContent = reviewContent;
        this.rating = rating;
        if (isSpoiler != null) {
            this.isSpoiler = isSpoiler;
        }
        this.isChanged = true; // 내용이 수정되었다는것을 체크
    }

    // 라뷰 삭제(Soft Delete)
    public void delete() {
        this.visibilityStatus = VisibilityStatus.DELETED;
    }

    public void changeVisibility(VisibilityStatus status) {
        this.visibilityStatus = status;
    }
}
