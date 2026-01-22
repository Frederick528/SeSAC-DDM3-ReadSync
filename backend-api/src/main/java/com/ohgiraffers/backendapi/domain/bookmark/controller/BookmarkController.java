package com.ohgiraffers.backendapi.domain.bookmark.controller;

import com.ohgiraffers.backendapi.domain.bookmark.dto.BookmarkRequestDTO;
import com.ohgiraffers.backendapi.domain.bookmark.dto.BookmarkResponseDTO;
import com.ohgiraffers.backendapi.domain.bookmark.entity.Bookmark;
import com.ohgiraffers.backendapi.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 생성 및 수정
    @PostMapping
    public ResponseEntity<Long> saveOrUpdate(@RequestBody BookmarkRequestDTO requestDTO) {
        return ResponseEntity.ok(bookmarkService.saveOrUpdate(requestDTO));
    }

    // 단건 조회
    @GetMapping("/{bookmarkId}")
    public ResponseEntity<BookmarkResponseDTO> getBookmark(@PathVariable Long bookmarkId) {
        return ResponseEntity.ok(bookmarkService.getBookmark(bookmarkId));
    }

    // 특정 서재(책)의 모든 북마크 조회
    @GetMapping("/library/{libraryId}")
    public ResponseEntity<List<BookmarkResponseDTO>> getLibraryBookmarks(@PathVariable Long libraryId) {
        return ResponseEntity.ok(bookmarkService.getBookmarksByLibrary(libraryId));
    }

    // 삭제
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.noContent().build();
    }
}
