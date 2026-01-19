package com.ohgiraffers.backendapi.domain.like.service;

import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import com.ohgiraffers.backendapi.domain.comment.repository.CommentRepository;
import com.ohgiraffers.backendapi.domain.like.dto.LikeResponseDTO;
import com.ohgiraffers.backendapi.domain.like.entity.Like;
import com.ohgiraffers.backendapi.domain.like.enums.LikeType;
import com.ohgiraffers.backendapi.domain.like.repository.LikeRepository;
import com.ohgiraffers.backendapi.domain.review.entity.Review;
import com.ohgiraffers.backendapi.domain.review.repository.ReviewRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//기존 내역 없음 -> 생성 (좋아요)
//기존 내역 있음 + 같은 타입 -> 삭제 (취소)
//기존 내역 있음 + 다른 타입 -> 변경 (좋아요 ↔ 싫어요 스위칭)

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // [ 댓글 좋아요/싫어요 토글 ]
    @Transactional
    public LikeResponseDTO toggleCommentLike(Long userId, Long commentId, LikeType requestType) {

        // 입력 값 검증(조회시 없으면 Exception)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 누른 적 있는지 확인
        Optional<Like> existingLike = likeRepository.findByComment_CommentIdAndUser_Id(commentId, userId);

        boolean isPressed;
        String message;

        if (existingLike.isPresent()) {
            // [A] 이미 Like 기록이 존재하는 경우
            Like like = existingLike.get();

            if(like.getLikeType() == requestType) {
                // [A-1] 좋아요 상태에서 좋아요 클릭(중복 클릭) -> 좋아요 취소
                likeRepository.delete(like);
                isPressed = false;
                message = "반응이 취소되었습니다.";
            } else {
                // [A-2] 좋아요 상태에서 싫어요 클릭(다른 것 클릭) -> 좋아요 타입 변경
                like.updateLikeType(requestType);
                isPressed = true;
                message = requestType.getDescription() + "로 변경되었습니다.";
            }
        } else {
            // [B] 처음 누른 경우 (생성)
            saveCommentLike(user, comment, requestType);
            isPressed = true;
            message = requestType.getDescription() + "가 반영되었습니다.";
        }

        // 최신 집계 반환(좋아요 반영/해제 여부, 어떤것이 반영되었는지, 좋아요 합계, 싫어요 합계)
        return buildResponse(isPressed, message,
                likeRepository.countByComment_CommentIdAndLikeType(commentId, LikeType.LIKE),
                likeRepository.countByComment_CommentIdAndLikeType(commentId, LikeType.DISLIKE));
    }

    // [ 리뷰 좋아요/싫어요 토글 ]
    @Transactional
    public LikeResponseDTO toggleReviewLike(Long userId, Long reviewId, LikeType requestType) {

        // 입력 값 검증(조회시 없으면 Exception)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 누른 적 있는지 확인
        Optional<Like> existingLike = likeRepository.findByReview_ReviewIdAndUser_Id(reviewId, userId);

        boolean isPressed;
        String message;

        if (existingLike.isPresent()) {
            // [A] 이미 Like 기록이 존재하는 경우
            Like like = existingLike.get();

            if(like.getLikeType() == requestType) {
                // [A-1] 좋아요 상태에서 좋아요 클릭(중복 클릭) -> 좋아요 취소
                likeRepository.delete(like);
                isPressed = false;
                message = "반응이 취소되었습니다.";
            } else {
                // [A-2] 좋아요 상태에서 싫어요 클릭(다른 것 클릭) -> 좋아요 타입 변경
                like.updateLikeType(requestType);
                isPressed = true;
                message = requestType.getDescription() + "로 변경되었습니다.";
            }
        } else {
            // [B] 처음 누른 경우 (생성)
            saveReviewLike(user, review, requestType);
            isPressed = true;
            message = requestType.getDescription() + "가 반영되었습니다.";
        }

        // 최신 집계 반환(좋아요 반영/해제 여부, 어떤것이 반영되었는지, 좋아요 합계, 싫어요 합계)
        // ※현재는 댓글 전체를 count 하고 있으나, 나중에는 review나 comment의 entity에 카운트 +1/-1만 하는 기능을 추가하여 반정규화로 할것.
        // 그러면 성능상 이점이 있다고 함.
        return buildResponse(isPressed, message,
                likeRepository.countByReview_ReviewIdAndLikeType(reviewId, LikeType.LIKE),
                likeRepository.countByReview_ReviewIdAndLikeType(reviewId, LikeType.DISLIKE));
    }



    // ---------- Helper Method ----------
    private void saveCommentLike(User user, Comment comment, LikeType type) {
        Like newLike = Like.builder()
                .user(user)
                .comment(comment)
                .review(null)
                .likeType(type)
                .build();
        likeRepository.save(newLike);
    }

    private void saveReviewLike(User user, Review review, LikeType type) {
        Like newLike = Like.builder()
                .user(user)
                .comment(null)
                .review(review)
                .likeType(type)
                .build();
        likeRepository.save(newLike);
    }

    private LikeResponseDTO buildResponse(boolean isPressed, String message, Long likes, Long dislikes) {
        return LikeResponseDTO.builder()
                .isPressed(isPressed)
                .message(message)
                .totalLikes(likes)
                .totalDislikes(dislikes)
                .build();
    }
}
