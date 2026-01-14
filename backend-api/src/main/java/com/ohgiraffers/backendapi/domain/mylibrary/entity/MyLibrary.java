package com.ohgiraffers.backendapi.domain.mylibrary.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.ReadingStatus;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MyLibrary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libraryId;

    @Column(nullable = false)
    private Long userId; // 실제 구현 시에는 User 엔티티와 @ManyToOne 관계를 맺는 것을 권장합니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OwnershipType ownershipType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus readingStatus = ReadingStatus.BEFORE_READING; // 기본값: 읽기 전

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 대여 시 만료일 (소유 시에는 매우 먼 미래 날짜 혹은 특정 규칙 적용)

    // 독서 상태 및 만료일 수정 로직
    public void updateStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }
}
