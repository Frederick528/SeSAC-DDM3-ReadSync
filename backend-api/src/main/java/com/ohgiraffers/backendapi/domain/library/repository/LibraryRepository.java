package com.ohgiraffers.backendapi.domain.library.repository;

import com.ohgiraffers.backendapi.domain.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    List<Library> findAllByUserIdAndDeletedAtIsNull(Long userId);

    List<Library> findAllByUserIdAndBook_Category_CategoryIdAndDeletedAtIsNull(Long userId, Long categoryId);
}
