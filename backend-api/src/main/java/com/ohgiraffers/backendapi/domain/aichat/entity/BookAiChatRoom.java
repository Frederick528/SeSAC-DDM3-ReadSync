package com.ohgiraffers.backendapi.domain.aichat.entity;

import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * AI 채팅방 엔티티
 * 사용자가 특정 챕터에 대해 AI와 대화하는 세션을 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_ai_chat_rooms")
@SQLRestriction("deleted_at IS NULL") // Soft delete된 채팅방 자동 필터링
public class BookAiChatRoom extends BaseTimeEntity {

    @Id
    @Column(name = "ai_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aiRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "title", length = 100)
    private String title;

    @Builder
    public BookAiChatRoom(User user, Chapter chapter, String title) {
        this.user = user;
        this.chapter = chapter;
        this.title = title;
    }

    /**
     * 채팅방 제목 업데이트
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * 기본 제목 생성 (챕터명 기반)
     */
    public static String generateDefaultTitle(Chapter chapter) {
        String chapterName = chapter.getChapterName();
        if (chapterName != null && !chapterName.isEmpty()) {
            return chapterName + " 대화";
        }
        return "챕터 " + chapter.getSequence() + " 대화";
    }
}
