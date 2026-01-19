package com.ohgiraffers.backendapi.domain.library.dto;

import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class LibraryResponseDTO {
    private Long libraryId;
    private Long userId;
    private Long bookId;
    private String bookTitle; // 편의를 위해 책 제목 포함
    private OwnershipType ownershipType;
    private BigDecimal totalProgress;
    private ReadingStatus readingStatus;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static LibraryResponseDTO from(Library lib) {
        return LibraryResponseDTO.builder()
                .libraryId(lib.getLibraryId())
                .userId(lib.getUser().getId())
                .bookId(lib.getBook().getBookId())
                .bookTitle(lib.getBook().getTitle())
                .ownershipType(lib.getOwnershipType())
                .totalProgress(lib.getTotalProgress())
                .readingStatus(lib.getReadingStatus())
                .expiresAt(lib.getExpiresAt())
                .createdAt(lib.getCreatedAt())
                .build();
    }
}

