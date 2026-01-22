package com.ohgiraffers.backendapi.domain.payment.service;

import com.ohgiraffers.backendapi.domain.payment.dto.BillingKeyRequest;
import com.ohgiraffers.backendapi.domain.payment.entity.PaymentMethod;
import com.ohgiraffers.backendapi.domain.payment.enums.PgProvider;
import com.ohgiraffers.backendapi.domain.payment.repository.PaymentMethodRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 결제 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final TossPaymentService tossPaymentService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    /**
     * 빌링키를 발급받아 결제 수단으로 등록합니다.
     * 
     * @param userId  사용자 ID
     * @param request 빌링키 발급 요청 정보
     */
    @Transactional
    public void registerPaymentMethod(Long userId, BillingKeyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 토스 API를 통해 빌링키 발급
        Map<String, Object> result = tossPaymentService.issueBillingKey(request);

        String billingKey = (String) result.get("billingKey");
        String cardCompany = "";
        String cardLast4 = "";

        if (result.containsKey("card")) {
            Map<String, Object> cardInfo = (Map<String, Object>) result.get("card");
            cardCompany = (String) cardInfo.get("issuerCode"); // 또는 company 등으로 변경 가능
            cardLast4 = (String) cardInfo.get("number");
            if (cardLast4 != null && cardLast4.length() >= 4) {
                cardLast4 = cardLast4.substring(cardLast4.length() - 4);
            }
        }

        // 기존 기본 결제 수단 해제 (필요시)
        paymentMethodRepository.findByUserAndIsDefaultTrueAndDeletedAtIsNull(user)
                .ifPresent(m -> {
                    m.updateDefaultStatus(false);
                    paymentMethodRepository.save(m);
                });

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .billingKey(billingKey)
                .pgProvider(PgProvider.TOSS)
                .cardCompany(cardCompany)
                .cardLast4(cardLast4)
                .customerKey(request.getCustomerKey())
                .isDefault(true)
                .build();

        paymentMethodRepository.save(paymentMethod);
    }
}
