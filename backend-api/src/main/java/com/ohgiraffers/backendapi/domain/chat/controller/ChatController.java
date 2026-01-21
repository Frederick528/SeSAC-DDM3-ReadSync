package com.ohgiraffers.backendapi.domain.chat.controller;

import com.ohgiraffers.backendapi.domain.chat.dto.ChatMessageRequest;
import com.ohgiraffers.backendapi.domain.chat.dto.ChatMessageResponse;
import com.ohgiraffers.backendapi.domain.chat.service.ChatLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat")
@Tag(name = "Chat API", description = "채팅 관련 API")
public class ChatController {

    private final ChatLogService chatLogService;

    // WebSocket : 실시간 메시지 전송 처리
    @MessageMapping("/chat/send")
    public void sendMesaage(@Payload ChatMessageRequest request, Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        log.info("WebSocket Message Received: userId={}, roomId={}", userId, request.getRoomId());

        chatLogService.sendMessage(userId, request);
    }

    // 채팅방 입장 시 최근 대화 목록 조회(50)
    @Operation(summary = "채팅방 입장 / 최근 대화 목록 조회")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getRecentMessages(
            @Parameter @PathVariable Long roomId
    ) {
        List<ChatMessageResponse> recentMessages = chatLogService.getRecentMessage(roomId);
        return ResponseEntity.ok(recentMessages);
    }

    // 50개 이전의 과거 메시지 조회
    @Operation(summary = "과거 대화 목록 조회")
    @GetMapping("/rooms/{roomId}/messages/history")
    public ResponseEntity<List<ChatMessageResponse>> getOldMessages(
            @Parameter @PathVariable Long roomId,
            @Parameter @RequestParam Long lastChatId
    ) {
        List<ChatMessageResponse> responses = chatLogService.getOldMessage(roomId, lastChatId);
        return ResponseEntity.ok(responses);
    }

}
