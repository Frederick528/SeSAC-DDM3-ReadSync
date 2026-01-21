package com.ohgiraffers.backendapi.domain.inquiryanswer.entity;

import com.ohgiraffers.backendapi.domain.inquiry.entity.Inquiry;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "inquiry_answers")
public class InquiryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 문의에 대한 답변인지 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    /** 답변한 관리자 ID */
    @Column(nullable = false)
    private Long adminUserId;   // 🔥 Long 타입

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected InquiryAnswer() {}

    public InquiryAnswer(Inquiry inquiry, Long adminUserId, String content) {
        this.inquiry = inquiry;
        this.adminUserId = adminUserId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
