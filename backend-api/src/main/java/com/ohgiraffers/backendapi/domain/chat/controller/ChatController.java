package com.ohgiraffers.backendapi.domain.chat.controller;

import com.ohgiraffers.backendapi.domain.chat.dto.ChatMessageRequest;
import com.ohgiraffers.backendapi.domain.chat.service.ChatLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat")
@Tag(name = "Chat API", description = "채팅 관련 API")
public class ChatController {

    private final ChatLogService chatLogService;

    // WebSocket : 실시간 메시지 전송 처리
//    @MessageMapping("/chat/send")
//    public void sendMesaage(@Payload ChatMessageRequest request, Principal principal)

}
