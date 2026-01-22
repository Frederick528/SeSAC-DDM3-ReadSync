package com.ohgiraffers.backendapi.domain.comment.repository;

import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 챕터 ID로 모든 댓글 조회 (오래된 순 정렬)
    List<Comment> findByChapter_ChapterIdOrderByCreatedAtAsc(Long ChapterId);

    // 사용자 ID로 본인 댓글 조회 (최신순 정렬)
    List<Comment> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
