package com.ohgiraffers.backendapi.domain.chat.entity;

import com.ohgiraffers.backendapi.domain.chat.enums.MessageType;
import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, updatable = false)
    private ReadingRoom readingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    // 메시지 타입 (TEXT, IMAGE, SYSTEM)
    @Column(name = "message_type", length = 20, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "content", columnDefinition = "TEXT", updatable = false)
    private String content;

    @Column(name = "image_url", length = 2083, updatable = false)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "send_at", nullable = false, updatable = false)
    private LocalDateTime sendAt;

    // 생성 메서드
    // 텍스트 메시지 생성용 (이미지는 Null)
    public static ChatLog createTextMessage(ReadingRoom room, User user, String content) {
        return ChatLog.builder()
                .readingRoom(room)
                .user(user)
                .content(content)
                .messageType(MessageType.TEXT)
                .imageUrl(null) // 명시적 Null
                .build();
    }

    // 이미지 메시지 생성용 (텍스트는 Null)
    public static ChatLog createImageMessage(ReadingRoom room, User user, String imageUrl) {
        return ChatLog.builder()
                .readingRoom(room)
                .user(user)
                .imageUrl(imageUrl)
                .messageType(MessageType.IMAGE)
                .content(null) // 명시적 Null
                .build();
    }
}
