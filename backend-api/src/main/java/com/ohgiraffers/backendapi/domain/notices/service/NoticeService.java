package com.ohgiraffers.backendapi.domain.notices.service;

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

    public Notice create(String title, String content, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return repository.save(
                Notice.builder()
                        .title(title)
                        .content(content)
                        .user(user)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<Notice> findAll() {
        return repository.findAll();
    }

    public Notice find(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.increaseViews();
        return notice;
    }

    public void delete(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.delete(); // Soft Delete
    }
}
