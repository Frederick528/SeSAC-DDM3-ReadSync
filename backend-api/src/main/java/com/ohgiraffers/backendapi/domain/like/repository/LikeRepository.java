package com.ohgiraffers.backendapi.domain.like.repository;

import com.ohgiraffers.backendapi.domain.like.entity.Like;
import com.ohgiraffers.backendapi.domain.like.enums.LikeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // [댓글 좋아요 토글]이미 좋아요를 눌렀는지 확인하여 (생성/취소) 분기 처리
    Optional<Like> findByComment_CommentIdAndUser_Id(Long commentId, Long userId);

    // [리뷰 좋아요 토글]이미 좋아요를 눌렀는지 확인하여 (생성/취소) 분기 처리
    Optional<Like> findByReview_ReviewIdAndUser_Id(Long reviewId, Long userId);

    // [댓글 좋아요 수 집계]특정 댓글의 총 좋아요 수를 실시간으로 계산
    Long countByComment_CommentIdAndLikeType(Long commentId, LikeType likeType);

    // [리뷰 좋아요 수 집계]특정 댓글의 총 좋아요 수를 실시간으로 계산
    Long countByReview_ReviewIdAndLikeType(Long reviewId, LikeType likeType);
}
