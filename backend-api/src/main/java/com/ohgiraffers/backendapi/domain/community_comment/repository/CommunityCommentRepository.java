package com.ohgiraffers.backendapi.domain.community_comment.repository;

import com.ohgiraffers.backendapi.domain.community_comment.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPost_PostId(Long postId);
}
