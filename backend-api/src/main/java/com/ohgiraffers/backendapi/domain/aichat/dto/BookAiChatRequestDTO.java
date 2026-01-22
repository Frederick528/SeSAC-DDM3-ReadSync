package com.ohgiraffers.backendapi.domain.aichat.dto;

import com.ohgiraffers.backendapi.domain.aichat.enums.ChatType;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 채팅 메시지 전송 요청 DTO
 */
@Getter
@NoArgsConstructor
public class BookAiChatRequestDTO {

    private String userMessage; // 필수: 사용자 질문
    private ChatType chatType; // 선택: 채팅 유형 (기본값: CONTENT_QA)
}
