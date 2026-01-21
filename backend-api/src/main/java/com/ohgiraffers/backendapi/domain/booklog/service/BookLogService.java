package com.ohgiraffers.backendapi.domain.booklog.service;

import com.ohgiraffers.backendapi.domain.book.dto.BookResponseDTO;
import com.ohgiraffers.backendapi.domain.booklog.dto.BookLogRequestDTO;
import com.ohgiraffers.backendapi.domain.booklog.dto.BookLogResponseDTO;
import com.ohgiraffers.backendapi.domain.booklog.entity.BookLog;
import com.ohgiraffers.backendapi.domain.booklog.repository.BookLogRepository;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.library.repository.LibraryRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookLogService {

    private final BookLogRepository bookLogRepository;
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;

    @EntityGraph(attributePaths = {"library", "library.user"})
    public List<BookLogResponseDTO> findAllByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다.");
        }

        // 2. Fetch Join이 적용된 메서드 호출로 N+1 방지
        return bookLogRepository.findAllByLibrary_User_Id(userId).stream()
                .map(BookLogResponseDTO::from)
                .toList();
    }


    @Transactional
    public BookLogResponseDTO saveOrUpdate(BookLogRequestDTO request) {
        LocalDate today = LocalDate.now();

        // 1. 오늘 날짜 + 해당 서재 항목의 로그가 있는지 확인
        return bookLogRepository.findByLibrary_LibraryIdAndReadDate(request.getLibraryId(), today)
                .map(existingLog -> {
                    // 2. 이미 있다면 기존 기록에 추가 (누적)
                    existingLog.updateLog(request.getReadTime(), request.getReadParagraph());
                    return BookLogResponseDTO.from(existingLog);
                })
                .orElseGet(() -> {
                    // 3. 없다면 새로 생성
                    Library library = libraryRepository.findById(request.getLibraryId())
                            .orElseThrow(() -> new IllegalArgumentException("서재 정보를 찾을 수 없습니다."));

//                    library.updateStatus(ReadingStatus.READING);

                    BookLog newLog = request.toEntity(library, today);
                    return BookLogResponseDTO.from(bookLogRepository.save(newLog));
                });
    }

    // 단건 상세 조회
    public BookLogResponseDTO getLog(Long bookLogId) {
        BookLog bookLog = bookLogRepository.findById(bookLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 독서 기록이 존재하지 않습니다."));
        return BookLogResponseDTO.from(bookLog);
    }

    // 삭제
    @Transactional
    public void deleteLog(Long bookLogId) {
        BookLog bookLog = bookLogRepository.findById(bookLogId)
                .orElseThrow(() -> new IllegalArgumentException("이미 존재하지 않는 도서 로그입니다."));
        bookLog.delete();
    }
}
