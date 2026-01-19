package com.ohgiraffers.backendapi.domain.inquiry.service;

import com.ohgiraffers.backendapi.domain.inquiry.dto.InquiryRequest;
import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.backendapi.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public Inquiry create(InquiryRequest request, Long userId) {

        Inquiry inquiry = new Inquiry(
                request.getTitle(),
                request.getContent(),
                userId
        );

        return inquiryRepository.save(inquiry);
    }
}
