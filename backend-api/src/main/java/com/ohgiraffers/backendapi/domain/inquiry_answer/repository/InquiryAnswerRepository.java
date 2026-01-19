package com.ohgiraffers.backendapi.domain.inquiry_answer.repository;

import com.ohgiraffers.backendapi.domain.inquiry_answer.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    List<InquiryAnswer> findByInquiry_Id(Long inquiryId);
}
