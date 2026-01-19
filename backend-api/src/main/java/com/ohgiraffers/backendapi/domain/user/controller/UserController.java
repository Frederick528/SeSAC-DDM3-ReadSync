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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @LogExecutionTime // 실행 시간 어노테이션
    @Operation(summary = "마이페이지(내 정보 조회)", description = "로그인한 유저의 상세 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse.Profile> getMyInfo(@CurrentUserId Long userId) {

        UserResponse.Profile response = userService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }

 /*   @LogExecutionTime
    @Operation(summary = "내 정보 입력/수정", description = "회원가입 직후 또는 마이페이지에서 유저의 상세 정보를 수정합니다.")
    @PostMapping("/me")
    public ResponseEntity<UserResponse.Detail> setMyInformation(
            @CurrentUserId Long userId,
            @RequestBody UserRequest.UserUpdate request
    ) {
        // 서비스에서 업데이트 로직 수행
        UserResponse.Detail response = authService.updateInformation(userId, request);

        return ResponseEntity.ok(response);
    }
*/


}