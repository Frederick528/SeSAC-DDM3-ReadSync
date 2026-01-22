package com.ohgiraffers.backendapi.domain.book.repository;

import com.ohgiraffers.backendapi.domain.book.entity.BookVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookVectorRepository extends JpaRepository<BookVector, Long> {

    // 입력된 벡터와 가장 유사한 도서 ID와 유사도 점수를 반환
    @NativeQuery(value = "SELECT book_id, (1 - (vector <=> cast(:queryVector as halfvec))) as score " +
            "FROM book_vectors " +
            "ORDER BY vector <=> cast(:queryVector as halfvec) " +
            "LIMIT :limit")
    List<Object[]> findSimilarBookIds(@Param("queryVector") String queryVector, @Param("limit") int limit);
}