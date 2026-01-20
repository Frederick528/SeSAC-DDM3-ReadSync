package com.ohgiraffers.backendapi.domain.payment.service;

import com.ohgiraffers.backendapi.domain.payment.dto.BillingKeyRequest;
import com.ohgiraffers.backendapi.domain.payment.dto.PaymentConfirmRequest;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 토스 페이먼츠 외부 API 연동을 담당하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final RestTemplate restTemplate;

    @Value("${toss.secret-key}")
    private String secretKey;

    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String BILLING_KEY_URL = "https://api.tosspayments.com/v1/billing/authorizations/issue";

    /**
     * 일반 결제 승인 요청
     */
    public Map<String, Object> confirmPayment(PaymentConfirmRequest request) {
        HttpHeaders headers = getHeaders();
        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", request.getPaymentKey());
        params.put("orderId", request.getOrderId());
        params.put("amount", request.getAmount());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(CONFIRM_URL, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            // 토스 API 응답 오류 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "결제 승인 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 빌링키 발급 요청 (정기 결제용)
     */
    public Map<String, Object> issueBillingKey(BillingKeyRequest request) {
        HttpHeaders headers = getHeaders();
        Map<String, Object> params = new HashMap<>();
        params.put("authKey", request.getAuthKey());
        params.put("customerKey", request.getCustomerKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(BILLING_KEY_URL, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "빌링키 발급 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 빌링키를 사용한 정기 결제 요청
     * 
     * @param billingKey  빌링키
     * @param customerKey 고객 키
     * @param amount      결제 금액
     * @param orderId     주문 번호
     * @param orderName   주문 명
     */
    public Map<String, Object> executeBillingPayment(String billingKey, String customerKey, Object amount,
            String orderId, String orderName) {
        String url = "https://api.tosspayments.com/v1/billing/" + billingKey;

        HttpHeaders headers = getHeaders();
        Map<String, Object> params = new HashMap<>();
        params.put("customerKey", customerKey);
        params.put("amount", amount);
        params.put("orderId", orderId);
        params.put("orderName", orderName);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "정기 결제 승인 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 토스 API 헤더 생성 (Basic 인증 포함)
     */
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setBasicAuth(encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
