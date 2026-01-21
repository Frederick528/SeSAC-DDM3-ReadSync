package com.ohgiraffers.backendapi.domain.community_post.repository;

import com.ohgiraffers.backendapi.domain.community_post.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
}
