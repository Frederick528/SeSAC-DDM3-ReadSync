package com.ohgiraffers.backendapi.domain.aichat.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.domain.aichat.enums.ChatType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * AI 채팅 메시지 엔티티
 * 사용자의 질문과 AI의 답변이 한 쌍으로 저장됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_ai_chats")
@SQLRestriction("deleted_at IS NULL") // Soft delete된 메시지 자동 필터링
public class BookAiChat extends BaseTimeEntity {

    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_room_id", nullable = false)
    private BookAiChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false, length = 20)
    private ChatType chatType;

    @Column(name = "user_msg", nullable = false, columnDefinition = "TEXT")
    private String userMessage;

    @Column(name = "ai_msg", nullable = false, columnDefinition = "TEXT")
    private String aiMessage;

    @Column(name = "rating", nullable = false)
    private Integer rating = 0;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Builder
    public BookAiChat(BookAiChatRoom chatRoom, User user, ChatType chatType,
            String userMessage, String aiMessage,
            Integer tokenCount, Integer responseTimeMs) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.chatType = (chatType != null) ? chatType : ChatType.CONTENT_QA;
        this.userMessage = userMessage;
        this.aiMessage = aiMessage;
        this.tokenCount = tokenCount;
        this.responseTimeMs = responseTimeMs;
    }

    /**
     * AI 답변 품질에 대한 사용자 평가 설정 (1~5점)
     */
    public void updateRating(Integer rating) {
        if (rating != null && rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }

    /**
     * AI 응답 메타데이터 업데이트
     */
    public void updateResponseMetadata(Integer tokenCount, Integer responseTimeMs) {
        this.tokenCount = tokenCount;
        this.responseTimeMs = responseTimeMs;
    }
}
