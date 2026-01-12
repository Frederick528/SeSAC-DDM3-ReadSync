package com.ohgiraffers.backendapi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        // 지금은 로그인이 없으니 일단 임시 ID(1L)나 0L을 반환합니다.
        return () -> Optional.of(1L);
    }
//    @Bean
//    public AuditorAware<Long> auditorProvider() {
//        return () -> {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication == null || !authentication.isAuthenticated()
//                    || authentication.getPrincipal().equals("anonymousUser")) {
//                return Optional.ofNullable(0L);
//            }
//
//            // [핵심] User 도메인의 Principal 객체에서 ID나 닉네임을 가져옵니다.
//            // 보통 SecurityContext에는 User의 고유 식별자나 ID가 들어있습니다.
//            return Optional.ofNullable(authentication.getName());
//        };
//    }
}