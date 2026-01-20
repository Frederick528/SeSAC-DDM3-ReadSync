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
public class Chapter extends BaseTimeEntity{
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
    private Integer sequence = 1;
    @Column(name = "book_content_path", nullable = false)
    private String bookContentPath;
    @Column(name = "is_embedded", nullable = false)
    private Boolean isEmbedded = false;

    @Builder
    public Chapter(Book book, String chapterName, Integer sequence, String bookContentPath) {
        this.book = book;
        this.chapterName = chapterName;
        this.sequence = sequence != null ? sequence : 1;
        this.bookContentPath = bookContentPath;
        this.isEmbedded = isEmbedded;
    }

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
        if (chapterName != null && !chapterName.isEmpty()) this.chapterName = chapterName;
        if (sequence != null) this.sequence = sequence;
    }
}
