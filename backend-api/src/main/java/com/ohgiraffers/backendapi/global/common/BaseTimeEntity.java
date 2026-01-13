package com.ohgiraffers.backendapi.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@MappedSuperclass // ❶
@EntityListeners(AuditingEntityListener.class) // ❷
public abstract class BaseTimeEntity {

    @CreatedDate // ❸
    @Column(updatable = false) // 생성 시간은 수정되지 않도록 설정
    private LocalDateTime createdAt;

    @LastModifiedDate // ❹
    private LocalDateTime updatedAt;
}
