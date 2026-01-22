package com.ohgiraffers.backendapi.domain.library.controller;

import com.ohgiraffers.backendapi.domain.library.dto.LibraryRequestDTO;
import com.ohgiraffers.backendapi.domain.library.dto.LibraryResponseDTO;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.library.service.LibraryService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/my-library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @PostMapping
    public ResponseEntity<Long> addToLibrary(@RequestBody LibraryRequestDTO request) {
        return ResponseEntity.ok(libraryService.addToLibrary(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LibraryResponseDTO>> getUserLibrary(@PathVariable Long userId) {
        return ResponseEntity.ok(libraryService.getUserLibrary(userId));
    }

    @GetMapping("me")
    public ResponseEntity<List<LibraryResponseDTO>> getMyBookLog(@CurrentUserId Long userId) {

        return ResponseEntity.ok(libraryService.getUserLibrary(userId));
    }


    @PatchMapping("/{libraryId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long libraryId, @RequestParam ReadingStatus status) {
        libraryService.updateReadingStatus(libraryId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<LibraryResponseDTO>> getLibraryByCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {

        List<LibraryResponseDTO> response = libraryService.getLibraryByUserIdAndCategoryId(userId, categoryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> delete(@PathVariable Long libraryId) {
        libraryService.deleteFromLibrary(libraryId);
        return ResponseEntity.noContent().build();
    }
}
