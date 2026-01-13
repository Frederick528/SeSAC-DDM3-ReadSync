package com.ohgiraffers.backendapi.domain.community_comments.repository;

import com.ohgiraffers.backendapi.domain.community_comments.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostId(Long postId);
}
