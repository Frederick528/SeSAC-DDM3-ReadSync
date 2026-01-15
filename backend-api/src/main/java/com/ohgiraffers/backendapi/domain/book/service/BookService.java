package com.ohgiraffers.backendapi.domain.book.service;

import com.ohgiraffers.backendapi.domain.book.dto.BookRequestDTO;
import com.ohgiraffers.backendapi.domain.book.dto.BookResponseDTO;
import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.category.entity.Category;
import com.ohgiraffers.backendapi.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository; // 카테고리 확인용

    // 1. 도서 등록
    @Transactional
    public Long createBook(BookRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Book book = request.toEntity(category);

        return bookRepository.save(book).getBookId();
    }

    // 2. 도서 단건 조회
    public BookResponseDTO getBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .filter(b -> b.getDeletedAt() == null) // 삭제된 도서 제외
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다."));
        return BookResponseDTO.from(book);
    }

    // 3. 도서 전체 조회
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAllByDeletedAtIsNull().stream()
                .map(BookResponseDTO::from)
                .toList();
    }

    // 4. 도서 수정
    @Transactional
    public void updateBook(Long bookId, BookRequestDTO request) {
        // 1. 기존 도서 조회 (삭제되지 않은 도서만)
        Book book = bookRepository.findById(bookId)
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("수정할 도서가 존재하지 않습니다."));

        // 2. 카테고리 정보가 변경되었다면 새로 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("변경하려는 카테고리가 존재하지 않습니다."));

        // 3. 엔티티 업데이트 (Dirty Checking 활용)
        book.update(category, request);
    }

    // 5. 도서 삭제 (Soft Delete)
    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("이미 존재하지 않는 도서입니다."));
        book.delete(); // BaseTimeEntity의 delete() 메서드 호출
    }
}
