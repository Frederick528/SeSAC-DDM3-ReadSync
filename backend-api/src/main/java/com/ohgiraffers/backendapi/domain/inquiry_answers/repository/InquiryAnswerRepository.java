package com.ohgiraffers.backendapi.domain.inquiry_answers.repository;

import com.ohgiraffers.backendapi.domain.inquiry_answers.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
}
