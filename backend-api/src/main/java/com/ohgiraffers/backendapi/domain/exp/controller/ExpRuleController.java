package com.ohgiraffers.backendapi.domain.exp.controller;

import com.ohgiraffers.backendapi.domain.exp.dto.ExpRuleRequestDTO;
import com.ohgiraffers.backendapi.domain.exp.dto.ExpRuleResponseDTO;
import com.ohgiraffers.backendapi.domain.exp.service.ExpRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/exp-rule")
@RequiredArgsConstructor
public class ExpRuleController {

    private final ExpRuleService expRuleService;

    @PostMapping
    public ResponseEntity<ExpRuleResponseDTO> createRule(@RequestBody ExpRuleRequestDTO requestDTO) {
        return ResponseEntity.ok(expRuleService.createRule(requestDTO));
    }

    @PutMapping("/{expRuleId}")
    public ResponseEntity<ExpRuleResponseDTO> updateRule(
            @PathVariable Long expRuleId,
            @RequestBody ExpRuleRequestDTO requestDTO) {
        return ResponseEntity.ok(expRuleService.updateRule(expRuleId, requestDTO));
    }

    @PatchMapping("/{expRuleId}/exp")
    public ResponseEntity<ExpRuleResponseDTO> updateExpAmount(
            @PathVariable Long expRuleId,
            @RequestParam Integer changeExpValue) {

        return ResponseEntity.ok(expRuleService.patchExp(expRuleId, changeExpValue));
    }

    @DeleteMapping("/{expRuleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long expRuleId) {
        expRuleService.deleteRule(expRuleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ExpRuleResponseDTO>> getAllRules() {
        return ResponseEntity.ok(expRuleService.findAllRules());
    }
}