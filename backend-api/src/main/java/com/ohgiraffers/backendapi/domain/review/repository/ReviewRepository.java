package com.ohgiraffers.backendapi.domain.review.repository;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.review.entity.Review;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 책의 리뷰 목록 조회 (페이징 지원)
    // 특정 상태(DELETED)가 아닌(Not) 모든 리뷰 조회
    Page<Review> findByBookAndVisibilityStatusNot(Book book, VisibilityStatus Status, Pageable pageable);

    // 사용자 ID로 본인 리뷰 조회 (페이징 지원, 삭제된 리뷰 제외)
    Page<Review> findByUser_IdAndVisibilityStatusNot(Long userId, VisibilityStatus status, Pageable pageable);
}
