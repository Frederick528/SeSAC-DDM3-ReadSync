package com.ohgiraffers.backendapi.domain.chat.dto;

import com.ohgiraffers.backendapi.domain.chat.entity.ChatLog;
import com.ohgiraffers.backendapi.domain.chat.enums.MessageType;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponse {

    @Schema(description = "채팅 ID", example = "105")
    private Long chatId;

    @Schema(description = "작성자 ID", example = "3")
    private Long senderId;

    @Schema(description = "작성자 닉네임", example = "책읽는기린")
    private String senderName;

    @Schema(description = "작성자 프로필 이미지", example = "http://k.kakaocdn.net/...")
    private String senderProfileImage;

    @Schema(description = "메시지 유형", example = "TEXT")
    private MessageType messageType;

    @Schema(description = "내용", example = "반갑습니다~")
    private String content;

    @Schema(description = "이미지 URL", example = "null")
    private String imageUrl;

    @Schema(description = "전송 시각", example = "2024-05-21T10:15:30")
    private LocalDateTime sendAt;

    // Entity -> DTO 변환 메서드 (팩토리 메서드)
    public static ChatMessageResponse from(ChatLog chatLog) {

        UserInformation userInfo = chatLog.getUser().getUserInformation();

        // 닉네임 안전하게 꺼내기 (Null 방어)
        String nickname = (userInfo != null && userInfo.getNickname() != null)
                ? userInfo.getNickname()
                : "알 수 없음"; // 혹은 user.getLoginId() 등 대체값

        // 프로필 이미지 안전하게 꺼내기
        String profileImage = (userInfo != null) ? userInfo.getProfileImage() : null;

        return ChatMessageResponse.builder()
                .chatId(chatLog.getChatId())
                .senderId(chatLog.getUser().getId())
                .senderName(userInfo.getNickname())
                .senderProfileImage(userInfo.getProfileImage())
                .messageType(chatLog.getMessageType())
                .content(chatLog.getContent())
                .imageUrl(chatLog.getImageUrl())
                .sendAt(chatLog.getSendAt())
                .build();
    }
}
