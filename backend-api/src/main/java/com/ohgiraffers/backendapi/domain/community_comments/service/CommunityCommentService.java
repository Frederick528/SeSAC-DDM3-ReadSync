package com.ohgiraffers.backendapi.domain.community_comments.service;

import com.ohgiraffers.backendapi.domain.community_comments.dto.CommunityCommentRequest;
import com.ohgiraffers.backendapi.domain.community_comments.entity.CommunityComment;
import com.ohgiraffers.backendapi.domain.community_comments.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRepository repository;

    public CommunityComment create(
            CommunityCommentRequest.Create request,
            Long postId,
            Long userId
    ) {
        return repository.save(
                CommunityComment.builder()
                        .content(request.getContent())
                        .parentId(request.getParentId())
                        .postId(postId)
                        .userId(userId)
                        .build()
        );
    }

    public List<CommunityComment> findByPostId(Long postId) {
        return repository.findByPostId(postId);
    }

    public CommunityComment update(Long commentId, CommunityCommentRequest.Update request) {
        CommunityComment comment = repository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        comment.update(request.getContent());
        return repository.save(comment);
    }

    public void delete(Long commentId) {
        repository.deleteById(commentId);
    }
}
