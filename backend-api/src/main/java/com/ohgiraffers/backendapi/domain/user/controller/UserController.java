package com.ohgiraffers.backendapi.domain.user.controller;

import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.service.UserService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import com.ohgiraffers.backendapi.global.common.annotation.LogExecutionTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @LogExecutionTime // 실행 시간 어노테이션
    @Operation(summary = "마이페이지(내 정보 조회)", description = "로그인한 유저의 상세 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse.Profile> getMyInfo(@CurrentUserId Long userId) {

        UserResponse.Profile response = userService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }
}