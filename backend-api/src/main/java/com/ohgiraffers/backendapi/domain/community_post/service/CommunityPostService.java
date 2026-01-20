package com.ohgiraffers.backendapi.domain.community_post.service;

import com.ohgiraffers.backendapi.domain.community_post.dto.CommunityPostRequest;
import com.ohgiraffers.backendapi.domain.community_post.entity.CommunityPost;
import com.ohgiraffers.backendapi.domain.community_post.repository.CommunityPostRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository repository;
    private final UserRepository userRepository;

    public CommunityPost create(CommunityPostRequest.Create request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return repository.save(
                CommunityPost.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .user(user)          // ✅ 핵심 수정
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

    /**
     * Soft Delete 적용
     */
    public void delete(Long postId) {
        CommunityPost post = find(postId);
        post.delete();          // BaseTimeEntity delete()
        repository.save(post);
    }
}
