package com.ohgiraffers.backendapi.domain.book.entity;


import com.ohgiraffers.backendapi.domain.book.dto.BookRequestDTO;
import com.ohgiraffers.backendapi.domain.book.enums.ViewPermission;
import com.ohgiraffers.backendapi.domain.category.entity.Category;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Book extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isAdultOnly = false;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String publisher;

    private LocalDate publishedDate;

    @Column(columnDefinition = "TEXT")
    private String coverUrl;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ViewPermission viewPermission = ViewPermission.FREE;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private String language;


    public void update(Category category, BookRequestDTO request) {
        this.category = category;
        this.title = request.getTitle();
        this.author = request.getAuthor();
        this.isAdultOnly = request.getIsAdultOnly();
        this.summary = request.getSummary();
        this.publisher = request.getPublisher();
        this.publishedDate = request.getPublishedDate();
        this.coverUrl = request.getCoverUrl();
        this.viewPermission = request.getViewPermission();
        this.price = request.getPrice();
        this.language = request.getLanguage();
    }
}
