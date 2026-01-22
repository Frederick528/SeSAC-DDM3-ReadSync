package com.ohgiraffers.backendapi.domain.review.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.review.dto.ReviewRequestDTO;
import com.ohgiraffers.backendapi.domain.review.dto.ReviewResponseDTO;
import com.ohgiraffers.backendapi.domain.review.entity.Review;
import com.ohgiraffers.backendapi.domain.review.repository.ReviewRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /* [1] 리뷰 생성 */
    @Transactional
    public Long createReview(Long userId, ReviewRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Book book = bookRepository.findByBookId(request.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .reviewContent(request.getContent())
                .isSpoiler(request.getIsSpoiler())
                .build();

        return reviewRepository.save(review).getReviewId();
    }

    // [2] 리뷰 단건 조회
    public ReviewResponseDTO getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 삭제되거나 정지된 리뷰는 조회 불가
        validateReviewStatus(review);

        return ReviewResponseDTO.from(review);
    }

    // [2-1] 특정 책의 리뷰 목록 조회(페이징)
    public Page<ReviewResponseDTO> getReviewByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        // 삭제된 리뷰를 제외한 리뷰 조회
        Page<Review> reviews = reviewRepository.findByBookAndVisibilityStatusNot(book, VisibilityStatus.DELETED,
                pageable);

        // Entity Page -> DTO Page 변환
        return reviews.map(ReviewResponseDTO::from);
    }

    // [2-2] 본인 리뷰 목록 조회(페이징)
    public Page<ReviewResponseDTO> getMyReviews(Long userId, Pageable pageable) {
        // 삭제된 리뷰를 제외한 본인 리뷰 조회
        Page<Review> reviews = reviewRepository.findByUser_IdAndVisibilityStatusNot(
                userId, VisibilityStatus.DELETED, pageable);

        return reviews.map(ReviewResponseDTO::from);
    }

    // [3] 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, Long userId, ReviewRequestDTO request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        validateReviewStatus(review); // 상태 검증 (DELETED, SUSPENDED 체크)
        validateOwner(review, userId); // 소유자 검증

        review.updateContent(request.getContent(), request.getRating(), request.getIsSpoiler());
    }

    // [4] 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        validateReviewStatus(review);
        validateOwner(review, userId);

        review.delete();
    }

    // [5] 관리자용 리뷰 전체 조회
    public Page<ReviewResponseDTO> getAllReviewsAdmin(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(ReviewResponseDTO::from);
    }

    // [6] 관리자용 리뷰 강제 삭제
    @Transactional
    public void deleteReviewAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        review.delete(); // Soft delete
    }

    /* [ Helper Method ] */

    // 리뷰 상태 검증(삭제됨 or 정지됨)
    private void validateReviewStatus(Review review) {
        if (review.getVisibilityStatus() == VisibilityStatus.DELETED) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        if (review.getVisibilityStatus() == VisibilityStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.REVIEW_SUSPENDED);
        }

    }

    // 리뷰 작성자인지 검증
    private void validateOwner(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.REVIEW_NOT_OWNER);
        }
    }
}
