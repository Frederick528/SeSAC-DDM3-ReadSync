package com.ohgiraffers.backendapi.domain.exp.service;

import com.ohgiraffers.backendapi.domain.exp.dto.ExpLogRequestDTO;
import com.ohgiraffers.backendapi.domain.exp.dto.ExpLogResponseDTO;
import com.ohgiraffers.backendapi.domain.exp.entity.ExpLog;
import com.ohgiraffers.backendapi.domain.exp.entity.ExpRule;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import com.ohgiraffers.backendapi.domain.exp.repository.ExpLogRepository;
import com.ohgiraffers.backendapi.domain.exp.repository.ExpRuleRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpLogService {

    private final ExpLogRepository expLogRepository;
    private final ExpRuleRepository expRuleRepository;
    private final UserRepository userRepository;

    private ExpRule findAppropriateRule(ActivityType type, Long categoryId) {
        // Case 1: 카테고리 ID가 들어온 경우 (예: 독서)
        if (categoryId != null) {
            return expRuleRepository.findByActivityTypeAndCategory_CategoryId(type, categoryId)
                    // 만약 특정 카테고리용 룰이 없다면, 해당 활동의 "일반 룰(Category IS NULL)"을 찾습니다.
                    .orElseGet(() -> expRuleRepository.findByActivityTypeAndCategoryIsNull(type)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    String.format("[%s] 활동에 대한 일반 규칙이 존재하지 않습니다.", type))));
        }

        // Case 2: 카테고리 ID가 처음부터 없는 경우 (예: 출석)
        return expRuleRepository.findByActivityTypeAndCategoryIsNull(type)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("[%s] 활동에 대한 규칙이 존재하지 않습니다.", type)));
    }

    @Transactional
    public ExpLogResponseDTO giveExperience(ExpLogRequestDTO requestDTO) {

        // 1. 넘어온 ID로 규칙이 실존하는지 확인
        ExpRule rule = findAppropriateRule(requestDTO.getActivityType(), requestDTO.getCategoryId());


        // 2. 중복 체크
        if (expLogRepository.existsByUser_IdAndExpRule_ExpRuleIdAndReferenceId(
                requestDTO.getUserId(), rule.getExpRuleId(), requestDTO.getReferenceId())) {
            throw new IllegalArgumentException("이미 처리된 활동입니다.");
        }

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));


        // 3. 저장
        ExpLog expLog = requestDTO.toEntity(user, rule);
        ExpLog savedLog = expLogRepository.save(expLog);

//        // 4. 유저 경험치 반영
//        user.addExperience(rule.getExp());

        return ExpLogResponseDTO.from(savedLog);
    }

    public List<ExpLogResponseDTO> findAllByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // EntityGraph 등을 사용해 ExpRule까지 한 번에 가져오는 것을 권장
        return expLogRepository.findAllByUser_Id(userId).stream()
                .map(ExpLogResponseDTO::from)
                .toList();
    }
}