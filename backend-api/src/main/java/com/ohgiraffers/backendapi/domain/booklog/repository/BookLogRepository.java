package com.ohgiraffers.backendapi.domain.booklog.repository;

import com.ohgiraffers.backendapi.domain.booklog.entity.BookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookLogRepository extends JpaRepository<BookLog, Long> {
    // 특정 서재 항목에 대해 해당 날짜의 기록이 이미 있는지 조회
    Optional<BookLog> findByLibrary_LibraryIdAndReadDate(Long libraryId, LocalDate readDate);
    List<BookLog> findAllByLibrary_User_Id(Long userId);
}
