package com.ohgiraffers.backendapi.domain.userbook.repository;

import com.ohgiraffers.backendapi.domain.userbook.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findAllByUserIdAndDeletedAtIsNull(Long userId);

    List<UserBook> findAllByUserIdAndBook_Category_CategoryIdAndDeletedAtIsNull(Long userId, Long categoryId);
}
