package com.ohgiraffers.backendapi.domain.book.controller;

import com.ohgiraffers.backendapi.domain.book.dto.BookRequestDTO;
import com.ohgiraffers.backendapi.domain.book.dto.BookResponseDTO;
import com.ohgiraffers.backendapi.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Long> createBook(@RequestBody BookRequestDTO request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDTO> getBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<String> updateBook(
            @PathVariable Long bookId,
            @RequestBody BookRequestDTO request) {

        bookService.updateBook(bookId, request);
        return ResponseEntity.ok("도서 정보가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
