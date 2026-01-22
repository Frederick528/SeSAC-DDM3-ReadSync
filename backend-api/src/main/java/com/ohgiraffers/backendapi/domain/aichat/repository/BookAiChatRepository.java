package com.ohgiraffers.backendapi.domain.aichat.repository;

import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChat;
import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 채팅 메시지 Repository
 */
@Repository
public interface BookAiChatRepository extends JpaRepository<BookAiChat, Long> {

    /**
     * 채팅방의 모든 메시지 조회 (시간순)
     */
    List<BookAiChat> findByChatRoomOrderByCreatedAtAsc(BookAiChatRoom chatRoom);

    /**
     * 채팅방의 메시지 페이징 조회 (최신순)
     */
    Page<BookAiChat> findByChatRoom(BookAiChatRoom chatRoom, Pageable pageable);

    /**
     * 채팅방 ID로 메시지 페이징 조회
     */
    @Query("SELECT c FROM BookAiChat c WHERE c.chatRoom.aiRoomId = :roomId ORDER BY c.createdAt ASC")
    Page<BookAiChat> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    /**
     * 채팅방 ID로 모든 메시지 조회 (시간순)
     */
    @Query("SELECT c FROM BookAiChat c WHERE c.chatRoom.aiRoomId = :roomId ORDER BY c.createdAt ASC")
    List<BookAiChat> findAllByRoomId(@Param("roomId") Long roomId);

    /**
     * 채팅방의 메시지 개수 조회
     */
    long countByChatRoom(BookAiChatRoom chatRoom);

    /**
     * 사용자 ID와 채팅방 ID로 메시지 존재 여부 확인 (권한 검증용)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM BookAiChat c " +
            "WHERE c.chatId = :chatId AND c.user.id = :userId")
    boolean existsByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
