package com.ohgiraffers.backendapi.domain.userbook.dto;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.userbook.entity.UserBook;
import com.ohgiraffers.backendapi.domain.userbook.enums.OwnershipType;
import com.ohgiraffers.backendapi.domain.userbook.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserBookRequestDTO {
    private Long userId;
    private Long bookId;
    private OwnershipType ownershipType;
    private ReadingStatus readingStatus;
    private LocalDateTime expiresAt;

    public UserBook toEntity(User user, Book book) {
        return UserBook.builder()
                .user(user)
                .book(book)
                .ownershipType(this.ownershipType)
                .readingStatus(this.readingStatus)
                .expiresAt(this.expiresAt)
                .build();
    }
}
