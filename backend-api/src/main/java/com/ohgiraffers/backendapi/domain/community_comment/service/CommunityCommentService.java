package com.ohgiraffers.backendapi.domain.community_comment.service;

import com.ohgiraffers.backendapi.domain.community_comment.dto.CommunityCommentRequest;
import com.ohgiraffers.backendapi.domain.community_comment.entity.CommunityComment;
import com.ohgiraffers.backendapi.domain.community_comment.repository.CommunityCommentRepository;
import com.ohgiraffers.backendapi.domain.community_post.entity.CommunityPost;
import com.ohgiraffers.backendapi.domain.community_post.repository.CommunityPostRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentService {

    private final CommunityCommentRepository repository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     */
    public CommunityComment create(
            CommunityCommentRequest.Create request,
            Long postId,
            Long userId
    ) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        CommunityComment parent = null;
        if (request.getParentId() != null) {
            parent = repository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
        }

        CommunityComment comment = CommunityComment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .parent(parent)
                .build();

        return repository.save(comment);
    }

    /**
     * 게시글별 댓글 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityComment> findByPostId(Long postId) {
        return repository.findByPost_PostId(postId);
    }

    /**
     * 댓글 수정
     */
    public CommunityComment update(Long commentId, CommunityCommentRequest.Update request) {
        CommunityComment comment = repository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        comment.update(request.getContent());
        return comment;
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    public void delete(Long commentId) {
        CommunityComment comment = repository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        comment.delete(); // ✅ BaseTimeEntity soft delete
    }
}
