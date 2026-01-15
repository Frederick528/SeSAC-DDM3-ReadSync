package com.ohgiraffers.backendapi.domain.inquiries.repository;

import com.ohgiraffers.backendapi.domain.inquiries.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
