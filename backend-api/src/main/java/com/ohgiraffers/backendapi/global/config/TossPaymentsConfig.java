package com.ohgiraffers.backendapi.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossPaymentsConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    private static final String TOSS_API_URL = "https://api.tosspayments.com";

    @Bean
    public WebClient tossPaymentWebClient() {
        String encodedSecretKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl(TOSS_API_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecretKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
