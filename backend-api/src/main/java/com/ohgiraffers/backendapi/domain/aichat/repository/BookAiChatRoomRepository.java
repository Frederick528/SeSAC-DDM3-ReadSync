package com.ohgiraffers.backendapi.domain.aichat.repository;

import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChatRoom;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI 채팅방 Repository
 */
@Repository
public interface BookAiChatRoomRepository extends JpaRepository<BookAiChatRoom, Long> {

    /**
     * 사용자의 모든 채팅방 조회 (최신순)
     */
    List<BookAiChatRoom> findByUserOrderByUpdatedAtDesc(User user);

    /**
     * 사용자의 채팅방 페이징 조회
     */
    Page<BookAiChatRoom> findByUser(User user, Pageable pageable);

    /**
     * 사용자 ID로 채팅방 페이징 조회
     */
    @Query("SELECT r FROM BookAiChatRoom r WHERE r.user.id = :userId ORDER BY r.updatedAt DESC")
    Page<BookAiChatRoom> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 챕터의 사용자 채팅방 조회
     */
    Optional<BookAiChatRoom> findByUserAndChapter(User user, Chapter chapter);

    /**
     * 특정 챕터의 사용자 채팅방 존재 여부 확인
     */
    boolean existsByUserAndChapter(User user, Chapter chapter);

    /**
     * 사용자 ID와 챕터 ID로 채팅방 조회
     */
    @Query("SELECT r FROM BookAiChatRoom r WHERE r.user.id = :userId AND r.chapter.chapterId = :chapterId")
    Optional<BookAiChatRoom> findByUserIdAndChapterId(@Param("userId") Long userId, @Param("chapterId") Long chapterId);
}
