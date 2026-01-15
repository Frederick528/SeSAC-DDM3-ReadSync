package com.ohgiraffers.backendapi.domain.userbook.controller;

import com.ohgiraffers.backendapi.domain.userbook.dto.UserBookRequestDTO;
import com.ohgiraffers.backendapi.domain.userbook.dto.UserBookResponseDTO;
import com.ohgiraffers.backendapi.domain.userbook.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.userbook.service.UserBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-book")
@RequiredArgsConstructor
public class UserBookController {
    private final UserBookService userBookService;

    @PostMapping
    public ResponseEntity<Long> addToUserBook(@RequestBody UserBookRequestDTO request) {
        return ResponseEntity.ok(userBookService.addToUserBook(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBookResponseDTO>> getUserBooksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userBookService.getUserBooksByUser(userId));
    }

    @PatchMapping("/{userBookId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long userBookId, @RequestParam ReadingStatus status) {
        userBookService.updateReadingStatus(userBookId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<UserBookResponseDTO>> getUserBooksByCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {

        List<UserBookResponseDTO> response = userBookService.getUserBooksByUserIdAndCategoryId(userId, categoryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userBookId}")
    public ResponseEntity<Void> delete(@PathVariable Long userBookId) {
        userBookService.deleteFromUserBook(userBookId);
        return ResponseEntity.noContent().build();
    }
}
