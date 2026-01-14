package com.ohgiraffers.backendapi.domain.book.repository;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByBookId(Long bookId);

    List<Book> findAllByDeletedAtIsNull();
}
