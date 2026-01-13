package com.ohgiraffers.backendapi.domain.book.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.common.type.ChatType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_ai_chats")
public class BookAiChat extends BaseTimeEntity {
    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;
    @Column(name = "conversation_group_id")
    private Long conversationGroupId;
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_id")
    private User userId;
    @Column(name = "chapter_id", nullable = false)
    private Chapter chapterId;
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false)
    private ChatType chatType;
    @Column(name = "", nullable = false)
    private
    @Column(name = "", nullable = false)
    private
    @Column(name = "", nullable = false)
    private

}
