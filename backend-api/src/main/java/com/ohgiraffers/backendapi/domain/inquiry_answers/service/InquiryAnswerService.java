package com.ohgiraffers.backendapi.domain.inquiry_answers.service;

import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiries.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.inquiry_answers.dto.InquiryAnswerRequest;
import com.ohgiraffers.backendapi.domain.inquiry_answers.entity.InquiryAnswer;
import com.ohgiraffers.backendapi.domain.inquiry_answers.repository.InquiryAnswerRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryAnswerService {

    private final InquiryAnswerRepository repository;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public InquiryAnswer create(Long inquiryId, Long userId, InquiryAnswerRequest.Create request) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        InquiryAnswer answer = InquiryAnswer.builder()
                .content(request.getContent())
                .inquiry(inquiry)
                .user(user)
                .build();

        return repository.save(answer);
    }

    @Transactional(readOnly = true)
    public List<InquiryAnswer> findByInquiry(Long inquiryId) {
        return repository.findByInquiry_InquiryId(inquiryId);
    }

    public InquiryAnswer update(Long answerId, InquiryAnswerRequest.Update request) {
        InquiryAnswer answer = repository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변 없음"));

        answer.update(request.getContent());
        return answer;
    }

    public void delete(Long answerId) {
        InquiryAnswer answer = repository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변 없음"));

        answer.delete(); // Soft Delete
    }
}
