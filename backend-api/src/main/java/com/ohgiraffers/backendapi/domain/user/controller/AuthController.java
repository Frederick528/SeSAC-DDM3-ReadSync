package com.ohgiraffers.backendapi.domain.user.controller;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.service.AuthService;
import com.ohgiraffers.backendapi.global.auth.dto.ReissueRequestDto;
import com.ohgiraffers.backendapi.global.auth.dto.TokenResponseDto;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import com.ohgiraffers.backendapi.global.common.annotation.LogExecutionTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증/로그인 관련 API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인/회원가입", description = "소셜(구글/카카오/네이버)에서 받은 정보로 로그인하거나 회원가입합니다.")
    @PostMapping("/social-login")
    public ResponseEntity<UserResponse.UserLoginResponse> socialLogin(@RequestBody @Valid UserRequest.Join request) {

        UserResponse.UserLoginResponse response = authService.socialLogin(request);

        return ResponseEntity.ok(response);
    }

    // [1] 관리자 가입: AdminSignup DTO 사용
    @PostMapping("/admin/signup")
    @Operation(summary = "관리자 가입 & 자동 로그인")
    public ResponseEntity<UserResponse.UserLoginResponse> createAdmin(@RequestBody UserRequest.AdminSignup request) {
        // 서비스에 DTO의 값들을 풀어서 전달
        UserResponse.UserLoginResponse response = authService.createAdmin(
                request.getLoginId(),
                request.getPassword(),
                request.getNickname()
        );
        return ResponseEntity.ok(response);
    }

    //  관리자 로그인
    @PostMapping("/login")
    @Operation(summary = "일반/관리자 로그인")
    public ResponseEntity<UserResponse.UserLoginResponse> login(@RequestBody UserRequest.Login request) {
        UserResponse.UserLoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth-success")
    @Operation(summary = "소셜 로그인 결과 확인 (테스트용)", description = "실제로는 프론트엔드로 리다이렉트 됩니다.")
    public String oauthSuccess(@RequestParam String accessToken, @RequestParam String refreshToken) {
        return "<h1>로그인 성공! 🥳</h1>" +
                "<p><b>Access Token:</b> " + accessToken + "</p>" +
                "<p><b>Refresh Token:</b> " + refreshToken + "</p>";
    }

    @PostMapping("/reissue")
    @Operation(summary = "Refresh Token 확인")
    public ResponseEntity<?> reissue(@RequestBody ReissueRequestDto request) {

        // 이제 request가 객체이므로 .getRefreshToken()이 가능해집니다.
        TokenResponseDto tokenDto = authService.reissue(request.getRefreshToken());

        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "DB에서 Refresh Token을 삭제하여 더 이상 토큰 갱신이 불가능하게 만듭니다.")
    public ResponseEntity<String> logout(@CurrentUserId Long userId) {

        authService.logout(userId);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}