package com.ohgiraffers.backendapi.domain.exp.aop;

import com.ohgiraffers.backendapi.domain.exp.annotation.AwardExp;
import com.ohgiraffers.backendapi.domain.exp.dto.ExpLogRequestDTO;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import com.ohgiraffers.backendapi.domain.exp.service.ExpLogService;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.enums.ReadingStatus;
import com.ohgiraffers.backendapi.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ExpAwardAspect {

    private final ExpLogService expLogService;
    private Logger log;

    @AfterReturning(pointcut = "@annotation(awardExp)", returning = "result")
    public void processExp(AwardExp awardExp, Object result) {
        // 1. 결과가 없으면(void 메서드 등) 아무것도 안 함
        if (result == null) return;

        try {
            // 2. 여기서만 경험치 로직 수행 (다른 팀원 코드에 영향 X)
            ExpLogRequestDTO requestDTO = mapToRequest(awardExp.type(), result);
            if (requestDTO != null) {
                expLogService.giveExperience(requestDTO);
            }
        } catch (Exception e) {
            // 3. 경험치 로직에서 에러가 나도 '원래 비즈니스 로직'은 성공해야 하므로 로그만 남김
            log.error("경험치 지급 중 오류 발생: {}", e.getMessage());
        }
    }

    // 타입별 매핑 로직은 별도 메서드로 분리해서 관리
    private ExpLogRequestDTO mapToRequest(ActivityType type, Object result) {
        // 1. 리뷰 작성 (WRITE_REVIEW)
        if (result instanceof Review review) {
            return ExpLogRequestDTO.builder()
                    .userId(review.getUser().getId())
                    .activityType(type)
                    .targetId(review.getReviewId())           // 어떤 리뷰인지
                    .referenceId(review.getBook().getBookId()) // 어느 책에 대한 리뷰인지 (중복 체크용)
                    .categoryId(review.getBook().getCategory() != null ?
                            review.getBook().getCategory().getCategoryId() : null)
                    .build();
        }

        if (result instanceof Library library) {
            // 상태가 완독(COMPLETED)이 아닐 때는 경험치를 주지 않음 (null 리턴)
            if (library.getReadingStatus() != ReadingStatus.COMPLETED) {
                return null;
            }

            return ExpLogRequestDTO.builder()
                    .userId(library.getUser().getId())
                    .activityType(type)
                    .targetId(library.getLibraryId())
                    .referenceId(library.getBook().getBookId())
                    .categoryId(library.getBook().getCategory() != null ?
                            library.getBook().getCategory().getCategoryId() : null)
                    .build();
        }

        // 매핑 정보를 찾을 수 없는 경우
        log.warn("매핑 실패: {} 활동에 대한 엔티티({}) 처리가 정의되지 않았습니다.", type, result.getClass().getSimpleName());
        return null;
    }
}
