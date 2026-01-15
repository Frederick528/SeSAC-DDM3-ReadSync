package com.ohgiraffers.backendapi.domain.userbook.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.domain.userbook.dto.UserBookRequestDTO;
import com.ohgiraffers.backendapi.domain.userbook.dto.UserBookResponseDTO;
import com.ohgiraffers.backendapi.domain.userbook.entity.UserBook;
import com.ohgiraffers.backendapi.domain.userbook.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.userbook.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long addToUserBook(UserBookRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Book book = bookRepository.findById(request.getBookId())
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다."));

        UserBook userBook = request.toEntity(user, book);

        return userBookRepository.save(userBook).getUserBookId();
    }

    public List<UserBookResponseDTO> getUserBooksByUser(Long userId) {
        return userBookRepository.findAllByUserIdAndDeletedAtIsNull(userId).stream()
                .map(UserBookResponseDTO::from)
                .toList();
    }

    @Transactional
    public void updateReadingStatus(Long userBookId, ReadingStatus status) {
        UserBook userBook = userBookRepository.findById(userBookId)
                .filter(ub ->ub.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("서재 정보를 찾을 수 없습니다."));
        userBook.updateStatus(status);

        if (status == ReadingStatus.COMPLETED) {
            // userExpService.giveExp(userBook.getUser().getId, userBook.getBook().getCategory().getExpByCategory());
        }
    }

    public List<UserBookResponseDTO> getUserBooksByUserIdAndCategoryId(Long userId, Long categoryId) {
        // Repository에서 유저 ID와 카테고리 ID로 필터링된 결과 조회
        List<UserBook> userBooks = userBookRepository
                .findAllByUserIdAndBook_Category_CategoryIdAndDeletedAtIsNull(userId, categoryId);

        return userBooks.stream()
                .map(UserBookResponseDTO::from)
                .toList();
    }

    @Transactional
    public void deleteFromUserBook(Long userBookId) {
        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제된 정보입니다."));
        userBook.delete(); // Soft Delete
    }
}
