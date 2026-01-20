package com.ohgiraffers.backendapi.domain.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long adminId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Notice() {}

    public Notice(String title, String content, Long adminId) {
        this.title = title;
        this.content = content;
        this.adminId = adminId;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
