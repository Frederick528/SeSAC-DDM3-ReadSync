package com.ohgiraffers.backendapi.domain.library.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.exp.annotation.AwardExp;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import com.ohgiraffers.backendapi.domain.library.dto.LibraryRequestDTO;
import com.ohgiraffers.backendapi.domain.library.dto.LibraryResponseDTO;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.library.repository.LibraryRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long addToLibrary(LibraryRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Book book = bookRepository.findById(request.getBookId())
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다."));

        Library library = request.toEntity(user, book);

        return libraryRepository.save(library).getLibraryId();
    }

    public List<LibraryResponseDTO> getUserLibrary(Long userId) {
        return libraryRepository.findAllByUserIdAndDeletedAtIsNull(userId).stream()
                .map(LibraryResponseDTO::from)
                .toList();
    }

    @AwardExp(type = ActivityType.READ_BOOK)
    @Transactional
    public void updateReadingStatus(Long libraryId, ReadingStatus status) {
        Library library = libraryRepository.findById(libraryId)
                .filter(l -> l.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("서재 정보를 찾을 수 없습니다."));
        library.updateStatus(status);
    }

    public List<LibraryResponseDTO> getLibraryByUserIdAndCategoryId(Long userId, Long categoryId) {
        // Repository에서 유저 ID와 카테고리 ID로 필터링된 결과 조회
        List<Library> libraries = libraryRepository
                .findAllByUserIdAndBook_Category_CategoryIdAndDeletedAtIsNull(userId, categoryId);

        return libraries.stream()
                .map(LibraryResponseDTO::from)
                .toList();
    }

    @Transactional
    public void deleteFromLibrary(Long libraryId) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("이미 삭제된 정보입니다."));
        library.delete(); // Soft Delete
    }
}
