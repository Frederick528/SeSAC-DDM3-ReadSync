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
    private int rentalDays;

    public Library toEntity(User user, Book book) {
        LocalDateTime expiresAt = null;

        if (this.ownershipType == OwnershipType.RENTED) {
            expiresAt = LocalDateTime.now().plusDays(this.rentalDays);
        }

        return Library.builder()
                .user(user)
                .book(book)
                .ownershipType(this.ownershipType)
                .readingStatus(ReadingStatus.BEFORE_READING)
                .expiresAt(expiresAt)
                .build();
    }
}
