package com.ohgiraffers.backendapi.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 설정
 * Python AI 서버와의 HTTP 통신을 위한 WebClient 빈을 제공합니다.
 */
@Configuration
public class WebClientConfig {

    @Value("${ai.server.url:http://localhost:8000}")
    private String aiServerUrl;

    /**
     * AI 서버용 WebClient 빈
     */
    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                .baseUrl(aiServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB 버퍼
                .build();
    }

    @Bean
    public WebClient embeddingServerWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8001") // 파이썬 임베딩 서버 주소
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
