package com.ohgiraffers.backendapi.domain.exp.service;

import com.ohgiraffers.backendapi.domain.category.entity.Category;
import com.ohgiraffers.backendapi.domain.category.repository.CategoryRepository;
import com.ohgiraffers.backendapi.domain.exp.dto.ExpRuleRequestDTO;
import com.ohgiraffers.backendapi.domain.exp.dto.ExpRuleResponseDTO;
import com.ohgiraffers.backendapi.domain.exp.entity.ExpRule;
import com.ohgiraffers.backendapi.domain.exp.repository.ExpRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpRuleService {

    private final ExpRuleRepository expRuleRepository;
    private final CategoryRepository categoryRepository;

    // 1. 규칙 생성
    @Transactional
    public ExpRuleResponseDTO createRule(ExpRuleRequestDTO requestDTO) {
        Category category = null;
        if (requestDTO.getCategoryId() != null && requestDTO.getCategoryId() != 0L) {
            category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        }

        ExpRule expRule = requestDTO.toEntity(category);
        return ExpRuleResponseDTO.from(expRuleRepository.save(expRule));
    }

    // 2. 규칙 수정
    @Transactional
    public ExpRuleResponseDTO updateRule(Long expRuleId, ExpRuleRequestDTO requestDTO) {
        ExpRule expRule = expRuleRepository.findById(expRuleId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 규칙이 존재하지 않습니다."));

        Category category = null;
        if (requestDTO.getCategoryId() != null && requestDTO.getCategoryId() != 0L) {
            category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        }

        // 엔티티 내부 update 메서드 활용 (더티 체킹)
        expRule.update(requestDTO.getActivityType(), requestDTO.getExp(), category);

        return ExpRuleResponseDTO.from(expRule);
    }

    // 3. 규칙 EXP만 수정
    @Transactional
    public ExpRuleResponseDTO patchExp(Long expRuleId, Integer changeExpValue) {
        // 1. 규칙 조회
        ExpRule expRule = expRuleRepository.findById(expRuleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경험치 규칙을 찾을 수 없습니다. ID: " + expRuleId));

        // 2. 경험치 값만 수정 (더티 체킹에 의해 자동 반영)
        expRule.updateExp(changeExpValue);

        return ExpRuleResponseDTO.from(expRule);
    }

    // 4. 규칙 삭제
    @Transactional
    public void deleteRule(Long expRuleId) {
        ExpRule expRule = expRuleRepository.findById(expRuleId)
                        .orElseThrow(()-> new IllegalArgumentException("삭제할 규칙이 존재하지 않습니다."));
        expRule.delete();
    }

    // 5. 전체 조회
    public List<ExpRuleResponseDTO> findAllRules() {
        return expRuleRepository.findAll().stream()
                .map(ExpRuleResponseDTO::from)
                .toList();
    }
}