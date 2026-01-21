package com.ohgiraffers.backendapi.domain.chat.dto;

import com.ohgiraffers.backendapi.domain.chat.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    @Schema(description = "채팅방 ID")
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long roomId;

    @Schema(description = "메시지 유형 (TEXT / IMAGE / SYSTEM)", example = "TEXT")
    @NotNull(message = "메시지 유형은 필수입니다.")
    private MessageType messageType;

    @Schema(description = "텍스트 내용 (이미지 전송 시 null 가능)")
    private String content;

    @Schema(description = "이미지 URL (텍스트 전송 시 null 가능)", example = "https://s3.aws.com/...")
    private String imageUrl;
}
