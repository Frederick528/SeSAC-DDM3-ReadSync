package com.ohgiraffers.backendapi.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RefreshToken {

    @Id
    @Column(name = "user_id") // pk, fk
    private Long userId;

    @Column(nullable = false)
    private String token;

    // 토큰 갱신 시 업데이트
    public void updateToken(String token) {
        this.token = token;
    }
}