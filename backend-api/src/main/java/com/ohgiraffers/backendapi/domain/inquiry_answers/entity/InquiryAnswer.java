package com.ohgiraffers.backendapi.domain.inquiry_answer.entity;

import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class InquiryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Inquiry inquiry;

    private Long adminUserId;

    private String content;

    public InquiryAnswer(Inquiry inquiry, Long adminUserId, String content) {
        this.inquiry = inquiry;
        this.adminUserId = adminUserId;
        this.content = content;
    }
}
