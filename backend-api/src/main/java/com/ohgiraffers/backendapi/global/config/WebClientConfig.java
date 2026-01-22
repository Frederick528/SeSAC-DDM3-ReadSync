package com.ohgiraffers.backendapi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient embeddingServerWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8000") // 파이썬 임베딩 서버 주소
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}