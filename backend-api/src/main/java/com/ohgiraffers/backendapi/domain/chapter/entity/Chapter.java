package com.ohgiraffers.backendapi.domain.chapter.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "chapters")
public class Chapter extends BaseTimeEntity {
    @Id
    @Column(name = "chapter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "chapter_name", length = 255)
    private String chapterName;

    @Column(name = "sequence", nullable = false)
    @Builder.Default
    private Integer sequence = 1;

    @Column(name = "book_content_path", nullable = false)
    private String bookContentPath;

    @Column(name = "is_embedded", nullable = false)
    @Builder.Default
    private Boolean isEmbedded = false;

    // 문단 개수 필드 (-1은 미설정 상태를 의미)
    @Column(name = "paragraphs", nullable = false)
    @Builder.Default
    private Integer paragraphs = -1;

    // 비즈니스 로직 메서드
    public void markAsEmbedded() {
        this.isEmbedded = true;
    }

    public void resetEmbeddingStatus() {
        this.isEmbedded = false;
    }

    public void updateFile(String newPath) {
        this.bookContentPath = newPath;
        this.isEmbedded = false; // 파일이 바뀌면 임베딩 상태 초기화
    }

    public void updateMetadata(String chapterName, Integer sequence) {
        if (chapterName != null && !chapterName.isEmpty())
            this.chapterName = chapterName;
        if (sequence != null)
            this.sequence = sequence;
    }

    // URL 업데이트 메서드 (URL을 bookContentPath로 저장)
    public void updateUrl(String newUrl) {
        this.bookContentPath = newUrl;
        this.isEmbedded = false; // URL이 바뀌면 임베딩 상태 초기화
    }

    // 문단 개수 업데이트 메서드
    public void updateParagraphs(Integer paragraphs) {
        if (paragraphs != null)
            this.paragraphs = paragraphs;
    }
}
