package com.ohgiraffers.backendapi.domain.mylibrary.repository;

import com.ohgiraffers.backendapi.domain.mylibrary.entity.MyLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyLibraryRepository extends JpaRepository<MyLibrary, Long> {
    List<MyLibrary> findAllByUserIdAndDeletedAtIsNull(Long userId);

    List<MyLibrary> findAllByUserIdAndBook_Category_CategoryIdAndDeletedAtIsNull(Long userId, Long categoryId);
}
