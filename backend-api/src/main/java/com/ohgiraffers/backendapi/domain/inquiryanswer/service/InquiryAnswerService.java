package com.ohgiraffers.backendapi.domain.inquiryanswer.service;

import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiry.enums.InquiryStatus;
import com.ohgiraffers.backendapi.domain.inquiry.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.inquiryanswer.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiryanswer.entity.InquiryAnswer;
import com.ohgiraffers.backendapi.domain.inquiryanswer.repository.InquiryAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryAnswerService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;

    /** 답변 등록 (관리자) */
    public InquiryAnswer create(
            Long inquiryId,
            Long adminUserId,
            InquiryAnswerRequest request
    ) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        inquiry.answer(); // 상태 ANSWERED 변경

        InquiryAnswer answer = new InquiryAnswer(
                inquiry,
                adminUserId,
                request.getContent()
        );

        return inquiryAnswerRepository.save(answer);
    }

    /** 답변 수정 (관리자) */
    public InquiryAnswer update(
            Long answerId,
            Long adminUserId,
            InquiryAnswerRequest request
    ) {
        InquiryAnswer answer = inquiryAnswerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변 없음"));

        if (!answer.getAdminUserId().equals(adminUserId)) {
            throw new IllegalStateException("수정 권한 없음");
        }

        answer.updateContent(request.getContent());
        return answer;
    }

    /** 답변 삭제 (관리자) */
    public void delete(Long answerId, Long adminUserId) {
        InquiryAnswer answer = inquiryAnswerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변 없음"));

        if (!answer.getAdminUserId().equals(adminUserId)) {
            throw new IllegalStateException("삭제 권한 없음");
        }

        inquiryAnswerRepository.delete(answer);
    }

    /** 답변 조회 (회원/관리자) */
    @Transactional(readOnly = true)
    public List<InquiryAnswer> findByInquiry(Long inquiryId) {
        return inquiryAnswerRepository.findByInquiry_Id(inquiryId);
    }
}
