package com.ohgiraffers.backendapi.domain.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 채팅방 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class BookAiChatRoomRequestDTO {

    private Long chapterId; // 필수: 채팅할 챕터 ID
    private String title; // 선택: 채팅방 제목 (미입력 시 자동 생성)
}
