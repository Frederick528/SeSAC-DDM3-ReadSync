package com.ohgiraffers.backendapi.domain.book.service;

import com.ohgiraffers.backendapi.domain.book.dto.BookVectorDTO;
import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.entity.BookVector;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.book.repository.BookVectorRepository;
import com.ohgiraffers.backendapi.domain.chapter.entity.ChapterVector;
import com.ohgiraffers.backendapi.domain.chapter.service.ChapterVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookVectorService {

    private final BookVectorRepository bookVectorRepository;
    private final BookRepository bookRepository;
    private final ChapterVectorService chapterVectorService;

    /**
     * 특정 도서 ID를 기준으로 유사한 도서를 추천합니다.
     */
    @Transactional(readOnly = true)
    public List<BookVectorDTO> getRecommendationsByBookId(Long bookId, int limit) {
        // 1. 기준이 되는 도서의 벡터를 가져옵니다.
        BookVector targetVector = bookVectorRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("해당 도서의 벡터 데이터가 존재하지 않습니다."));

        // 2. float[]을 pgvector가 인식할 수 있는 문자열 "[0.1, 0.2, ...]" 형태로 변환합니다.
        String vectorString = Arrays.toString(targetVector.getVector());

        // 3. 유사도 검색을 수행합니다. (본인 제외 로직은 Repository SQL에서 처리하거나 여기서 필터링)
        return getRecommendationsByVector(vectorString, limit + 1).stream()
                .filter(dto -> !dto.getBookId().equals(bookId)) // 자기 자신 제외
                .limit(limit)
                .toList();
    }

    /**
     * 특정 벡터(문자열)를 기준으로 유사한 도서를 추천합니다.
     */
    @Transactional(readOnly = true)
    public List<BookVectorDTO> getRecommendationsByVector(String vector, int limit) {
        // Repository의 Native Query 결과를 가져옵니다. (결과: [book_id, score])
        List<Object[]> results = bookVectorRepository.findSimilarBookIds(vector, limit);

        return results.stream().map(result -> {
            Long id = ((Number) result[0]).longValue();
            Double score = ((Number) result[1]).doubleValue();

            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("도서 정보를 찾을 수 없습니다. ID: " + id));

            return BookVectorDTO.from(book);
        }).toList();
    }

    /**
     * 챕터 벡터들을 취합하여 북 벡터를 생성/업데이트합니다.
     */
    @Transactional
    public void createBookVectorFromChapters(Long bookId) {
        List<float[]> chapterVectors = chapterVectorService.getChapterVectorsForBook(bookId);

        if (chapterVectors.isEmpty()) {
            throw new RuntimeException("임베딩된 챕터가 없어 북 벡터를 생성할 수 없습니다.");
        }

        float[] averagedVector = calculateAverage(chapterVectors);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("도서가 존재하지 않습니다."));

        BookVector bookVector = BookVector.builder()
                .book(book)
                .vector(averagedVector)
                .build();

        bookVectorRepository.save(bookVector);
    }

    private float[] calculateAverage(List<float[]> vectors) {
        int dim = vectors.getFirst().length;
        float[] avg = new float[dim];
        for (float[] v : vectors) {
            for (int i = 0; i < dim; i++) avg[i] += v[i];
        }
        for (int i = 0; i < dim; i++) avg[i] /= vectors.size();
        return avg;
    }
}