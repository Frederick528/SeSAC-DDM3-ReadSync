package com.ohgiraffers.backendapi.domain.inquiry.entity;

import com.ohgiraffers.backendapi.domain.inquiry.enums.InquiryStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private Long userId;

    private LocalDateTime createdAt;

    protected Inquiry() {}

    public Inquiry(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.status = InquiryStatus.WAIT;
        this.createdAt = LocalDateTime.now();
    }

    /** 문의 수정 */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /** 답변 완료 처리 */
    public void answer() {
        this.status = InquiryStatus.ANSWERED;
    }
}
