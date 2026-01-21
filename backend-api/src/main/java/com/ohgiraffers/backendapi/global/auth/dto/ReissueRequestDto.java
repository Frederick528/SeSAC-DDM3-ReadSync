package com.ohgiraffers.backendapi.global.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequestDto {
    private String refreshToken;
}