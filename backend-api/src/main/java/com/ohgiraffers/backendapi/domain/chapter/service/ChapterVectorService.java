package com.ohgiraffers.backendapi.domain.chapter.service;

import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterVectorService {

    private final ChapterVectorRepository chapterVectorRepository;

    @Transactional(readOnly = true)
    public List<float[]> getChapterVectorsForBook(Long bookId) {
        // 도서에 속한 모든 챕터 벡터를 가져와서 북 벡터 서비스에 전달
        return chapterVectorRepository.findAllVectorsByBookId(bookId);
    }

    // 챕터별 유사도 검색이 필요할 경우 추가 로직 구현 가능
}