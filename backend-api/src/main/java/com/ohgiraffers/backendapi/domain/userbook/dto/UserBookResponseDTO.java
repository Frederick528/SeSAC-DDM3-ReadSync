package com.ohgiraffers.backendapi.domain.userbook.dto;

import com.ohgiraffers.backendapi.domain.userbook.entity.UserBook;
import com.ohgiraffers.backendapi.domain.userbook.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.userbook.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserBookResponseDTO {
    private Long userBookId;
    private Long userId;
    private Long bookId;
    private String bookTitle; // 편의를 위해 책 제목 포함
    private OwnershipType ownershipType;
    private ReadingStatus readingStatus;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static UserBookResponseDTO from(UserBook userBook) {
        return UserBookResponseDTO.builder()
                .userBookId(userBook.getUserBookId())
                .userId(userBook.getUser().getId())
                .bookId(userBook.getBook().getBookId())
                .bookTitle(userBook.getBook().getTitle())
                .ownershipType(userBook.getOwnershipType())
                .readingStatus(userBook.getReadingStatus())
                .expiresAt(userBook.getExpiresAt())
                .createdAt(userBook.getCreatedAt())
                .build();
    }
}

