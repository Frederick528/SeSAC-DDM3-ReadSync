package com.ohgiraffers.backendapi.domain.inquiries.service;

import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiries.repository.InquiryRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public Inquiry create(String title, String content, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return inquiryRepository.save(
                Inquiry.builder()
                        .title(title)
                        .content(content)
                        .user(user)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<Inquiry> findMyInquiries(Long userId) {
        return inquiryRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
