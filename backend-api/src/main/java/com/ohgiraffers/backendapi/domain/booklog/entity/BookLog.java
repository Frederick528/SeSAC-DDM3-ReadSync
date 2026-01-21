package com.ohgiraffers.backendapi.domain.booklog.entity;

import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "book_logs")
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BookLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    @Column(nullable = false)
    private LocalDate readDate;

    @Builder.Default
    @Column(nullable = false)
    private Integer readTime = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer readParagraph = 0;


    public void updateLog(Integer readTime, Integer readParagraph) {
        this.readTime += readTime;
        this.readParagraph += readParagraph;
    }
}