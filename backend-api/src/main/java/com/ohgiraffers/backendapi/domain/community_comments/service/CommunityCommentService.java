package com.ohgiraffers.backendapi.domain.community_comments.service;

import com.ohgiraffers.backendapi.domain.community_comments.entity.CommunityComment;
import com.ohgiraffers.backendapi.domain.community_comments.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;

    public CommunityComment create(CommunityComment comment) {
        return communityCommentRepository.save(comment);
    }

    public List<CommunityComment> findByPostId(Long postId) {
        return communityCommentRepository.findByPostId(postId);
    }
}
