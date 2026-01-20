package com.ohgiraffers.backendapi.domain.bookmark.service;

import com.ohgiraffers.backendapi.domain.bookmark.dto.BookmarkRequestDTO;
import com.ohgiraffers.backendapi.domain.bookmark.dto.BookmarkResponseDTO;
import com.ohgiraffers.backendapi.domain.bookmark.entity.Bookmark;
import com.ohgiraffers.backendapi.domain.bookmark.repository.BookmarkRepository;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.exp.annotation.AwardExp;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.library.repository.LibraryRepository;
import com.ohgiraffers.backendapi.domain.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final LibraryService libraryService;
    private final LibraryRepository libraryRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public Long saveOrUpdate(BookmarkRequestDTO dto) {
        Chapter chapter = chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("챕터가 없습니다."));
        int totalLength = chapter.getSequence();

        // 2. 북마크 객체 확보 (없으면 생성 후 저장하여 영속성 확보)
        Bookmark bookmark = bookmarkRepository.findByLibrary_LibraryIdAndChapter_ChapterId(dto.getLibraryId(), dto.getChapterId())
                .orElseGet(() -> {
                    Library library = libraryRepository.findById(dto.getLibraryId())
                            .orElseThrow(() -> new IllegalArgumentException("서재를 찾을 수 없습니다."));

                    String emptyMask = "0".repeat(totalLength);
                    byte[] maskBytes = emptyMask.getBytes(StandardCharsets.UTF_8);
                    libraryService.updateReadingStatus(library.getLibraryId() ,ReadingStatus.READING);
                    // DTO를 통해 신규 엔티티 생성 후 즉시 저장
                    return bookmarkRepository.save(dto.toEntity(library, chapter, maskBytes));
                });

        // 2. 엔티티에게 업데이트를 명령 (DTO만 던지면 끝!)
        bookmark.syncReadStatus(dto.getReadParagraphIndices(), dto.getLastReadPos());

        return bookmark.getBookmarkId();
    }

    @Transactional(readOnly = true)
    public BookmarkResponseDTO getBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("북마크 정보를 찾을 수 없습니다."));
        return BookmarkResponseDTO.from(bookmark);
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> getBookmarksByLibrary(Long libraryId) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("서재 정보를 찾을 수 없습니다."));

        return bookmarkRepository.findAllByLibrary(library).stream()
                .map(BookmarkResponseDTO::from)
                .toList();
    }

    @Transactional
    public void deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("북마크 정보를 찾을 수 없습니다."));
        bookmark.delete();
    }
}
