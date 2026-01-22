package com.ohgiraffers.backendapi.domain.chapter.repository;

import com.ohgiraffers.backendapi.domain.chapter.entity.ChapterVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterVectorRepository extends JpaRepository<ChapterVector, Long> {

    // 특정 도서(bookId)에 속한 모든 챕터의 벡터 리스트를 가져옴 (북 벡터 합성용)
    @NativeQuery(value = "SELECT cv.vector FROM chapter_vectors cv " +
            "JOIN chapters c ON cv.chapter_id = c.chapter_id " +
            "WHERE c.book_id = :bookId")
    List<float[]> findAllVectorsByBookId(@Param("bookId") Long bookId);

    // 특정 챕터와 유사한 다른 챕터 검색
    @NativeQuery(value = "SELECT chapter_id FROM chapter_vectors " +
            "ORDER BY vector <=> cast(:queryVector as halfvec) " +
            "LIMIT :limit")
    List<Long> findSimilarChapterIds(@Param("queryVector") String queryVector, @Param("limit") int limit);
}
