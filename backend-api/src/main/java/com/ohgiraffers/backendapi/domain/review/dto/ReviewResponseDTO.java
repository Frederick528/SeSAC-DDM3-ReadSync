package com.ohgiraffers.backendapi.domain.review.dto;

import com.ohgiraffers.backendapi.domain.review.entity.Review;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;
    private String writerName;
    private String bookTitle;
    private Integer rating;
    private String content;
    private Boolean isChanged;
    private Boolean isSpoiler;
    private VisibilityStatus visibilityStatus;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .writerName(review.getUser().getUserInformation().getNickname())
                .bookTitle(review.getBook().getTitle())
                .rating(review.getRating())
                .content(review.getReviewContent())
                .isChanged(review.getIsChanged())
                .isSpoiler(review.getIsSpoiler())
                .visibilityStatus(review.getVisibilityStatus())
                .likeCount(review.getLikeCount())
                .dislikeCount(review.getDislikeCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
