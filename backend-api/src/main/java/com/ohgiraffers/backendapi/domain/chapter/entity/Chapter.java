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

    @Column(name = "paragraphs", nullable = false)
    @Builder.Default
    private Integer paragraphs = -1;

    @Builder
    public Chapter(Book book, String chapterName, Integer sequence, String bookContentPath, Integer paragraphs) {
        this.book = book;
        this.chapterName = chapterName;
        this.sequence = sequence != null ? sequence : 1;
        this.bookContentPath = bookContentPath;
        this.isEmbedded = false;
        this.paragraphs = paragraphs != null ? paragraphs : -1;
    }

    // 비즈니스 로직 메서드
    public void markAsEmbedded() {
        this.isEmbedded = true;
    }

    public void resetEmbeddingStatus() {
        this.isEmbedded = false;
    }

    public void updateFile(String newPath, Integer newParagraphs) {
        this.bookContentPath = newPath;
        this.isEmbedded = false; // 파일이 바뀌면 임베딩 상태 초기화
        if (newParagraphs != null) {
            this.paragraphs = newParagraphs;
        }
    }

    public void updateMetadata(String chapterName, Integer sequence) {
        if (chapterName != null && !chapterName.isEmpty())
            this.chapterName = chapterName;
        if (sequence != null)
            this.sequence = sequence;
    }

    public void updateParagraphs(Integer count) {
        if (count != null) {
            this.paragraphs = count;
        }
    }
}
