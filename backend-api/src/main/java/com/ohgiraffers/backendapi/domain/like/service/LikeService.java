package com.ohgiraffers.backendapi.domain.like.service;

import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import com.ohgiraffers.backendapi.domain.like.dto.LikeResponseDTO;
import com.ohgiraffers.backendapi.domain.like.entity.Like;
import com.ohgiraffers.backendapi.domain.like.enums.LikeType;
import com.ohgiraffers.backendapi.domain.like.repository.LikeRepository;
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
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // [ 댓글 좋아요/싫어요 토글 ]
    public LikeResponseDTO toggleCommentLike(Long userId, Long commentId, LikeType requestType) {

        // 입력 값 검증(조회시 없으면 Exception)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 누른 적 있는지 확인
        Optional<Like>
    }
}
