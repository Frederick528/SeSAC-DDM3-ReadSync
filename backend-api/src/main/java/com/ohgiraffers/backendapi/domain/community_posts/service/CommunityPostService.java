package com.ohgiraffers.backendapi.domain.community_posts.service;

import com.ohgiraffers.backendapi.domain.community_posts.dto.CommunityPostRequest;
import com.ohgiraffers.backendapi.domain.community_posts.entity.CommunityPost;
import com.ohgiraffers.backendapi.domain.community_posts.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository repository;

    public CommunityPost create(CommunityPostRequest.Create request, Long userId) {
        return repository.save(
                CommunityPost.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .userId(userId)
                        .views(0)
                        .likeCount(0)
                        .report(0)
                        .build()
        );
    }

    public CommunityPost find(Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
    }

    public List<CommunityPost> findAll() {
        return repository.findAll();
    }

    public CommunityPost update(Long postId, CommunityPostRequest.Update request) {
        CommunityPost post = find(postId);
        post.update(request.getTitle(), request.getContent());
        return repository.save(post);
    }

    public void delete(Long postId) {
        repository.deleteById(postId);
    }
}
