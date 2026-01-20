package com.ohgiraffers.backendapi.domain.inquiryanswer.repository;

import com.ohgiraffers.backendapi.domain.inquiryanswer.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    // 문의 ID로 여러 답변 조회
    List<InquiryAnswer> findByInquiry_Id(Long inquiryId);
}
