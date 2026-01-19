package com.ohgiraffers.backendapi.domain.inquiry.service;

import com.ohgiraffers.backendapi.domain.inquiry.dto.InquiryRequest;
import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    /** 문의 등록 (회원) */
    public Inquiry create(InquiryRequest request, Long userId) {
        Inquiry inquiry = new Inquiry(
                request.getTitle(),
                request.getContent(),
                userId
        );
        return inquiryRepository.save(inquiry);
    }

    /** 내 문의 목록 조회 */
    @Transactional(readOnly = true)
    public List<Inquiry> findByUser(Long userId) {
        return inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /** 문의 상세 조회 */
    @Transactional(readOnly = true)
    public Inquiry find(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));
    }
}
