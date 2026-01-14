package com.ohgiraffers.backendapi.domain.mylibrary.dto;

import com.ohgiraffers.backendapi.domain.mylibrary.entity.MyLibrary;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyLibraryResponseDTO {
    private Long libraryId;
    private Long userId;
    private Long bookId;
    private String bookTitle; // 편의를 위해 책 제목 포함
    private OwnershipType ownershipType;
    private ReadingStatus readingStatus;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static MyLibraryResponseDTO from(MyLibrary lib) {
        return MyLibraryResponseDTO.builder()
                .libraryId(lib.getLibraryId())
                .userId(lib.getUserId())
                .bookId(lib.getBook().getBookId())
                .bookTitle(lib.getBook().getTitle())
                .ownershipType(lib.getOwnershipType())
                .readingStatus(lib.getReadingStatus())
                .expiresAt(lib.getExpiresAt())
                .createdAt(lib.getCreatedAt())
                .build();
    }
}

