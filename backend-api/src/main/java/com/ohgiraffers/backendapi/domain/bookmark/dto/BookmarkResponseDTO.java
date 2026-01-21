package com.ohgiraffers.backendapi.domain.bookmark.dto;

import com.ohgiraffers.backendapi.domain.bookmark.entity.Bookmark;
import lombok.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Getter
@Builder
@AllArgsConstructor
public class BookmarkResponseDTO {

    private Long bookmarkId;
    private Long libraryId;
    private Long chapterId;
    private Integer lastReadPos;
    private BigDecimal progress;
//    private byte[] readMask;

    // Entity -> DTO 변환 메서드
    public static BookmarkResponseDTO from(Bookmark bookmark) {
        return BookmarkResponseDTO.builder()
                .bookmarkId(bookmark.getBookmarkId())
                .libraryId(bookmark.getLibrary().getLibraryId())
                .chapterId(bookmark.getChapter().getChapterId())
                .lastReadPos(bookmark.getLastReadPos())
                .progress(bookmark.getProgress())
//                .readMask(bookmark.getReadMask())
                .build();
    }
}
