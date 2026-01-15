package com.ohgiraffers.backendapi.domain.inquiry_answers.service;

import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiries.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.inquiry_answers.entity.InquiryAnswer;
import com.ohgiraffers.backendapi.domain.inquiry_answers.repository.InquiryAnswerRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryAnswerService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository repository;
    private final UserRepository userRepository;

    public InquiryAnswer create(Long inquiryId, String content, Long adminId) {

        User admin = userRepository.findById(adminId)
                .orElseThrow();

        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자만 답변 가능");
        }

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow();

        inquiry.markAnswered();

        return repository.save(
                InquiryAnswer.builder()
                        .content(content)
                        .inquiry(inquiry)
                        .admin(admin)
                        .build()
        );
    }
}
