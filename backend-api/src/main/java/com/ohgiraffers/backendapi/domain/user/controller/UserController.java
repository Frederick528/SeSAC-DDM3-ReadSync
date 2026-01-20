package com.ohgiraffers.backendapi.domain.user.controller;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.service.AuthService;
import com.ohgiraffers.backendapi.domain.user.service.UserService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import com.ohgiraffers.backendapi.global.common.annotation.LogExecutionTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 유저의 상세 정보를 조회합니다.")
    public ResponseEntity<UserResponse.UserInfo> getMyProfile(@CurrentUserId Long userId) {
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "닉네임, 프로필 사진 등을 수정합니다.")
    public ResponseEntity<UserResponse.UserInfo> updateProfile(
            @CurrentUserId Long userId,
            @RequestBody UserRequest.UpdateProfile request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "계정을 영구 삭제하고 로그아웃 처리합니다.")
    public ResponseEntity<String> withdraw(@CurrentUserId Long userId) {
        userService.withdraw(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}