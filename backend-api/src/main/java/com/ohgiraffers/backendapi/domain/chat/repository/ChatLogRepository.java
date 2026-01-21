package com.ohgiraffers.backendapi.domain.chat.repository;

import com.ohgiraffers.backendapi.domain.chat.entity.ChatLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    // 방 입장시 최신 message N개 조회
    @Query("select c from ChatLog c " +
            "join fetch c.user u " +
            "left join fetch u.userInformation " +
            "where c.readingRoom.roomId = :roomId " +
            "order by c.chatId desc")
    List<ChatLog> findRecentMessage(@Param("roomId") Long roomId, Pageable pageable);


    // 스크롤 올릴 때 특정 메시지(lastChatId)보다 오래된 메시지 n개 조회
    @Query("select c from ChatLog c " +
            "join fetch c.user u " +
            "left join fetch u.userInformation " +
            "where c.readingRoom.roomId = :roomId " +
            "and c.chatId < :lastChatId " +
            "order by c.chatId desc ")
    List<ChatLog> findOldMessage(@Param("roomId") Long roomId, @Param("lastChatId") Long lastChatId, Pageable pageable);
}
