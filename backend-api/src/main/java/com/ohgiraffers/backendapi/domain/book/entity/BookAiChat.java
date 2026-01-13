package com.ohgiraffers.backendapi.domain.book.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.common.enums.ChatType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_ai_chats")
public class BookAiChat extends BaseTimeEntity {
    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_room_id")
    private BookAiChatRoom aiRoomId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false, length = 20)
    private ChatType chatType;
    @Column(name = "user_msg", nullable = false)
    private String userMessage;
    @Column(name = "ai_msg", nullable = false)
    private String aiMessage;
    @Column(name = "rating", nullable = false)
    private Integer rating = 0;

    @Builder
    public BookAiChat(BookAiChatRoom aiRoomId, User userId, ChatType chatType, String userMessage, String aiMessage) {
        this.aiRoomId = aiRoomId;
        this.userId = userId;
        this.chatType = chatType = (chatType != null) ? chatType : ChatType.CONTENT_QA;
        this.userMessage = userMessage;
        this.aiMessage = aiMessage;
    }

    // AI 답변 품질에 대한 사용자의 평가
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
