package com.ohgiraffers.backendapi.domain.notice.service;

import com.ohgiraffers.backendapi.domain.notice.dto.NoticeRequest;
import com.ohgiraffers.backendapi.domain.notice.entity.Notice;
import com.ohgiraffers.backendapi.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /** 공지 등록 (관리자) */
    @Transactional
    public Notice create(NoticeRequest request, Long adminId) {
        Notice notice = new Notice(
                request.getTitle(),
                request.getContent(),
                adminId
        );
        return noticeRepository.save(notice);
    }

    /** 공지 수정 (관리자) */
    @Transactional
    public Notice update(Long noticeId, NoticeRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.update(request.getTitle(), request.getContent());
        return notice;
    }

    /** 공지 삭제 (관리자) */
    @Transactional
    public void delete(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));
        noticeRepository.delete(notice);
    }

    /** 공지 목록 조회 (회원/관리자) */
    @Transactional(readOnly = true)
    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    /** 공지 상세 조회 (회원/관리자) */
    @Transactional(readOnly = true)
    public Notice find(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));
    }
}
