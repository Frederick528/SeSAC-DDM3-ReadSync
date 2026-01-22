package com.ohgiraffers.backendapi.domain.aichat.dto;

import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChat;
import com.ohgiraffers.backendapi.domain.aichat.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * AI 채팅 메시지 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class BookAiChatResponseDTO {

    private Long chatId;
    private Long aiRoomId;
    private String userMessage;
    private String aiMessage;
    private ChatType chatType;
    private Integer rating;
    private Integer tokenCount;
    private Integer responseTimeMs;
    private LocalDateTime createdAt;

    /**
     * Entity -> DTO 변환
     */
    public static BookAiChatResponseDTO from(BookAiChat chat) {
        return BookAiChatResponseDTO.builder()
                .chatId(chat.getChatId())
                .aiRoomId(chat.getChatRoom().getAiRoomId())
                .userMessage(chat.getUserMessage())
                .aiMessage(chat.getAiMessage())
                .chatType(chat.getChatType())
                .rating(chat.getRating())
                .tokenCount(chat.getTokenCount())
                .responseTimeMs(chat.getResponseTimeMs())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
