package com.ohgiraffers.backendapi.domain.mylibrary.dto;

import com.ohgiraffers.backendapi.domain.mylibrary.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyLibraryRequestDTO {
    private Long userId;
    private Long bookId;
    private OwnershipType ownershipType;
    private ReadingStatus readingStatus;
    private LocalDateTime expiresAt;
}
