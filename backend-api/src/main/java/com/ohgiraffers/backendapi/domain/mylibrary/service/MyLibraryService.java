package com.ohgiraffers.backendapi.domain.mylibrary.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.mylibrary.dto.MyLibraryRequestDTO;
import com.ohgiraffers.backendapi.domain.mylibrary.dto.MyLibraryResponseDTO;
import com.ohgiraffers.backendapi.domain.mylibrary.entity.MyLibrary;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.mylibrary.repository.MyLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyLibraryService {
    private final MyLibraryRepository myLibraryRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long addToLibrary(MyLibraryRequestDTO request) {
        Book book = bookRepository.findById(request.getBookId())
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다."));

        MyLibrary library = MyLibrary.builder()
                .userId(request.getUserId())
                .book(book)
                .ownershipType(request.getOwnershipType())
                .readingStatus(request.getReadingStatus())
                .expiresAt(request.getExpiresAt())
                .build();

        return myLibraryRepository.save(library).getLibraryId();
    }

    public List<MyLibraryResponseDTO> getUserLibrary(Long userId) {
        return myLibraryRepository.findAllByUserIdAndDeletedAtIsNull(userId).stream()
                .map(lib -> MyLibraryResponseDTO.builder()
                        .libraryId(lib.getLibraryId())
                        .userId(lib.getUserId())
                        .bookId(lib.getBook().getBookId())
                        .bookTitle(lib.getBook().getTitle())
                        .ownershipType(lib.getOwnershipType())
                        .readingStatus(lib.getReadingStatus())
                        .expiresAt(lib.getExpiresAt())
                        .createdAt(lib.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateReadingStatus(Long libraryId, ReadingStatus status) {
        MyLibrary library = myLibraryRepository.findById(libraryId)
                .filter(l -> l.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("서재 정보를 찾을 수 없습니다."));
        library.updateStatus(status);

        if (status == ReadingStatus.COMPLETED) {
            // userExpService.giveExp(library.getUserId(), library.getBook().getCategory().getExpByCategory());
        }
    }

    public List<MyLibraryResponseDTO> getLibraryByUserIdAndCategoryId(Long userId, Long categoryId) {
        // Repository에서 유저 ID와 카테고리 ID로 필터링된 결과 조회
        List<MyLibrary> libraries = myLibraryRepository
                .findAllByUserIdAndBook_Category_CategoryIdAndDeletedAtIsNull(userId, categoryId);

        return libraries.stream()
                .map(MyLibraryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFromLibrary(Long libraryId) {
        MyLibrary library = myLibraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제된 정보입니다."));
        library.delete(); // Soft Delete
    }
}
