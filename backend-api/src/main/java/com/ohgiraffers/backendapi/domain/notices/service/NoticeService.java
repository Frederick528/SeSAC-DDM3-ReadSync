package com.ohgiraffers.backendapi.domain.notices.service;

import com.ohgiraffers.backendapi.domain.notices.dto.NoticeRequest;
import com.ohgiraffers.backendapi.domain.notices.entity.Notice;
import com.ohgiraffers.backendapi.domain.notices.repository.NoticeRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository repository;
    private final UserRepository userRepository;

    /** 공지 생성 */
    public Notice create(NoticeRequest.Create request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return repository.save(
                Notice.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .user(user)
                        .build()
        );
    }

    /** 공지 전체 조회 */
    @Transactional(readOnly = true)
    public List<Notice> findAll() {
        return repository.findAll();
    }

    /** 공지 상세 조회 + 조회수 증가 */
    public Notice find(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.increaseViews();
        return notice;
    }

    /** 공지 수정 */
    public Notice update(Long noticeId, NoticeRequest.Update request) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.update(request.getTitle(), request.getContent());
        return notice;
    }

    /** 공지 삭제 (Soft Delete) */
    public void delete(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.delete();
    }
}
