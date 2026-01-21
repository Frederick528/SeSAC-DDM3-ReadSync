package com.ohgiraffers.backendapi.domain.bookmark.repository;

import com.ohgiraffers.backendapi.domain.bookmark.entity.Bookmark;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByLibrary_LibraryIdAndChapter_ChapterId(Long libraryId, Long chapterId);
    List<Bookmark> findAllByLibrary(Library library);
}
