package com.ohgiraffers.backendapi.domain.library.dto;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LibraryRequestDTO {
    private Long userId;
    private Long bookId;
    private OwnershipType ownershipType;
    private ReadingStatus readingStatus;
    private LocalDateTime expiresAt;

    public Library toEntity(User user, Book book) {
        return Library.builder()
                .user(user)
                .book(book)
                .ownershipType(this.ownershipType)
                .readingStatus(this.readingStatus)
                .expiresAt(this.expiresAt)
                .build();
    }
}
