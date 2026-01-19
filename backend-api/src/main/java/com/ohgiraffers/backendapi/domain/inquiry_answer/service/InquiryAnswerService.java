package com.ohgiraffers.backendapi.domain.inquiry_answer.service;

import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiry.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.inquiry_answer.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiry_answer.entity.InquiryAnswer;
import com.ohgiraffers.backendapi.domain.inquiry_answer.repository.InquiryAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryAnswerService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository answerRepository;

    /** 관리자 답변 작성 */
    public InquiryAnswer create(
            Long inquiryId,
            Long adminUserId,
            InquiryAnswerRequest request
    ) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        // 상태 변경 (WAIT → ANSWERED)
        inquiry.answer();

        InquiryAnswer answer = new InquiryAnswer(
                inquiry,
                adminUserId,
                request.getContent()
        );

        return answerRepository.save(answer);
    }

    /** 답변 조회 (회원/비회원) */
    @Transactional(readOnly = true)
    public List<InquiryAnswer> findByInquiry(Long inquiryId) {
        return answerRepository.findByInquiry_Id(inquiryId);
    }
}
