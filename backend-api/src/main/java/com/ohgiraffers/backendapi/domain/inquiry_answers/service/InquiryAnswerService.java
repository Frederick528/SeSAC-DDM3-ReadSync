package com.ohgiraffers.backendapi.domain.inquiry_answer.service;

import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiry.enums.InquiryStatus;
import com.ohgiraffers.backendapi.domain.inquiry.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.inquiry_answer.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiry_answer.entity.InquiryAnswer;
import com.ohgiraffers.backendapi.domain.inquiry_answer.repository.InquiryAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryAnswerService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository answerRepository;

    public InquiryAnswer create(Long inquiryId, Long adminUserId, InquiryAnswerRequest request) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        inquiry.answer(); // 상태 변경 (WAIT → ANSWERED)

        InquiryAnswer answer = new InquiryAnswer(
                inquiry,
                adminUserId,
                request.getContent()
        );

        return answerRepository.save(answer);
    }
}
