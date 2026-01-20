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

    /** 문의 수정 (본인만 가능) */
    @Transactional
    public Inquiry update(Long inquiryId, Long userId, InquiryRequest request) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        if (!inquiry.getUserId().equals(userId)) {
            throw new SecurityException("본인 문의만 수정할 수 있습니다.");
        }

        inquiry.update(request.getTitle(), request.getContent());
        return inquiry;
    }

    /** 문의 삭제 (본인만 가능) */
    @Transactional
    public void delete(Long inquiryId, Long userId) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        if (!inquiry.getUserId().equals(userId)) {
            throw new SecurityException("본인 문의만 삭제할 수 있습니다.");
        }

        inquiryRepository.delete(inquiry);
    }
}
