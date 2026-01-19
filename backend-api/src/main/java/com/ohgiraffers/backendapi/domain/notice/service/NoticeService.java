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
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /** 공지 생성 (ADMIN) */
    public Notice create(NoticeRequest.Create request, Long adminId) {
        Notice notice = new Notice(
                request.getTitle(),
                request.getContent(),
                adminId
        );
        return noticeRepository.save(notice);
    }

    /** 공지 전체 조회 */
    @Transactional(readOnly = true)
    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    /** 공지 상세 조회 + 조회수 증가 */
    public Notice find(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.increaseViews();
        return notice;
    }
}
