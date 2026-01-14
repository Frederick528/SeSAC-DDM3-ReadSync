package com.ohgiraffers.backendapi.domain.mylibrary.controller;

import com.ohgiraffers.backendapi.domain.mylibrary.dto.MyLibraryRequestDTO;
import com.ohgiraffers.backendapi.domain.mylibrary.dto.MyLibraryResponseDTO;
import com.ohgiraffers.backendapi.domain.mylibrary.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.mylibrary.service.MyLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/my-library")
@RequiredArgsConstructor
public class MyLibraryController {
    private final MyLibraryService myLibraryService;

    @PostMapping
    public ResponseEntity<Long> addToLibrary(@RequestBody MyLibraryRequestDTO request) {
        return ResponseEntity.ok(myLibraryService.addToLibrary(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyLibraryResponseDTO>> getMyLibrary(@PathVariable Long userId) {
        return ResponseEntity.ok(myLibraryService.getUserLibrary(userId));
    }

    @PatchMapping("/{libraryId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long libraryId, @RequestParam ReadingStatus status) {
        myLibraryService.updateReadingStatus(libraryId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<MyLibraryResponseDTO>> getMyLibraryByCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {

        List<MyLibraryResponseDTO> response = myLibraryService.getLibraryByUserIdAndCategoryId(userId, categoryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> delete(@PathVariable Long libraryId) {
        myLibraryService.deleteFromLibrary(libraryId);
        return ResponseEntity.noContent().build();
    }
}
