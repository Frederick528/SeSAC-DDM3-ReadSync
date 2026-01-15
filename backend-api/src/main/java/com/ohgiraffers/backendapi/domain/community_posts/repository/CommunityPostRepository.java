package com.ohgiraffers.backendapi.domain.community_posts.repository;

import com.ohgiraffers.backendapi.domain.community_posts.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
}
