package com.ohgiraffers.backendapi.domain.book.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_ai_chat_rooms")
public class BookAiChatRoom extends BaseTimeEntity {
    @Id
    @Column(name = "ai_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aiRoomId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapterId;

    @Builder
    public BookAiChatRoom(User userId, Chapter chapterId) {
        this.userId = userId;
        this.chapterId = chapterId;
    }
}
