package com.ohgiraffers.backendapi.domain.inquiries.service;

import com.ohgiraffers.backendapi.domain.inquiries.dto.InquiryRequest;
import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiries.enums.InquiryStatus;
import com.ohgiraffers.backendapi.domain.inquiries.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    /**
     * 문의 등록
     */
    public Inquiry create(InquiryRequest.Create request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Inquiry inquiry = Inquiry.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(InquiryStatus.WAIT)
                .user(user)
                .build();

        return inquiryRepository.save(inquiry);
    }

    /**
     * 내 문의 목록 조회
     */
    public List<Inquiry> findMyInquiries(Long userId) {
        return inquiryRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
