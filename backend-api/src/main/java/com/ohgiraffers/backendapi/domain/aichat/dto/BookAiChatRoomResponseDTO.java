package com.ohgiraffers.backendapi.domain.aichat.dto;

import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * AI 채팅방 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class BookAiChatRoomResponseDTO {

    private Long aiRoomId;
    private Long chapterId;
    private String chapterName;
    private String bookTitle;
    private String title;
    private Long messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> DTO 변환
     */
    public static BookAiChatRoomResponseDTO from(BookAiChatRoom room) {
        return BookAiChatRoomResponseDTO.builder()
                .aiRoomId(room.getAiRoomId())
                .chapterId(room.getChapter().getChapterId())
                .chapterName(room.getChapter().getChapterName())
                .bookTitle(room.getChapter().getBook().getTitle())
                .title(room.getTitle())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    /**
     * Entity -> DTO 변환 (메시지 개수 포함)
     */
    public static BookAiChatRoomResponseDTO from(BookAiChatRoom room, Long messageCount) {
        return BookAiChatRoomResponseDTO.builder()
                .aiRoomId(room.getAiRoomId())
                .chapterId(room.getChapter().getChapterId())
                .chapterName(room.getChapter().getChapterName())
                .bookTitle(room.getChapter().getBook().getTitle())
                .title(room.getTitle())
                .messageCount(messageCount)
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
