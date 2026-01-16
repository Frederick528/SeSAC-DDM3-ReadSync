package com.ohgiraffers.backendapi.domain.user.controller;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증/로그인 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인/회원가입", description = "소셜(구글/카카오/네이버)에서 받은 정보로 로그인하거나 회원가입합니다.")
    @PostMapping("/social-login")
    public ResponseEntity<UserResponse.Login> socialLogin(@RequestBody @Valid UserRequest.Join request) {

        UserResponse.Login response = authService.socialLogin(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일반/관리자 로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<UserResponse.Login> login(@RequestBody @Valid UserRequest.Login request) {

        UserResponse.Login response = authService.login(request);

        return ResponseEntity.ok(response);
    }

}