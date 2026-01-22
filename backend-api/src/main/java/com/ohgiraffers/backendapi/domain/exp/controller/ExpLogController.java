package com.ohgiraffers.backendapi.domain.exp.controller;

import com.ohgiraffers.backendapi.domain.exp.dto.ExpLogRequestDTO;
import com.ohgiraffers.backendapi.domain.exp.dto.ExpLogResponseDTO;
import com.ohgiraffers.backendapi.domain.exp.service.ExpLogService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/exp")
@RequiredArgsConstructor
public class ExpLogController {

    private final ExpLogService expLogService;

    /**
     * 경험치 지급
     * 성공 시 지급된 로그 상세 정보를 반환합니다.
     */
    @PostMapping("/give")
    public ResponseEntity<ExpLogResponseDTO> giveExperience(@RequestBody ExpLogRequestDTO requestDTO) {
        return ResponseEntity.ok(expLogService.giveExperience(requestDTO));
    }

    /**
     * 유저별 경험치 로그 전체 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpLogResponseDTO>> getExpLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(expLogService.findAllByUser(userId));
    }

    /**
     * 자신의 경험치 로그 전체 조회
     */
    @GetMapping("/me")
    public ResponseEntity<List<ExpLogResponseDTO>> getMyExpLogs(@CurrentUserId Long userId) {
        return ResponseEntity.ok(expLogService.findAllByUser(userId));
    }
}