package com.ohgiraffers.backendapi.domain.chat.service;

import com.ohgiraffers.backendapi.domain.chat.dto.ChatMessageRequest;
import com.ohgiraffers.backendapi.domain.chat.entity.ChatLog;
import com.ohgiraffers.backendapi.domain.chat.enums.MessageType;
import com.ohgiraffers.backendapi.domain.chat.repository.ChatLogRepository;
import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.repository.ReadingRoomRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatLogServiceTest {

    @InjectMocks
    private ChatLogService chatLogService;

    @Mock
    private ChatLogRepository chatLogRepository;
    @Mock
    private ReadingRoomRepository readingRoomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("메시지 전송 시 DB 저장 및 Redis 발행이 정상적으로 호출된다.")
    void sendMessageTest() {
        // given
        Long userId = 1L;
        Long roomId = 100L;

        ChatMessageRequest request = ChatMessageRequest.builder()
                .roomId(roomId)
                .messageType(MessageType.TEXT)
                .content("테스트 내용입니다.")
                .build();

        // Mocking (가짜 객체 행동 정의)

        // 1. 가짜 UserInformation 생성 (닉네임 보유)
        UserInformation mockUserInfo = UserInformation.builder()
                .nickname("테스트닉네임")
                .build();

        // 2. 가짜 User 생성 시 정보 주입
        User mockUser = User.builder()
                .id(userId)
                .userInformation(mockUserInfo) // ★ 핵심: 이게 없어서 에러가 났던 것임!
                .build();

        ReadingRoom mockRoom = ReadingRoom.builder().roomId(roomId).build();
        ChatLog mockChatLog = ChatLog.createTextMessage(mockRoom, mockUser, "테스트");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(readingRoomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));
        when(chatLogRepository.save(any(ChatLog.class))).thenReturn(mockChatLog);

        // when
        chatLogService.sendMessage(userId, request);

        // then
        // 1. DB 저장이 호출되었는가?
        verify(chatLogRepository).save(any(ChatLog.class));

        // 2. Redis로 메시지를 쐈는가? (channel, message)
        verify(redisTemplate).convertAndSend(anyString(), any(Object.class));
    }
}