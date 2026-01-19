package com.ohgiraffers.backendapi.global.config;

import com.ohgiraffers.backendapi.global.auth.jwt.JwtAuthenticationFilter;
import com.ohgiraffers.backendapi.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

               /* .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // 인증불필요
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )*/

                // 임시 모드 허용 (관리자 모드로 변경)
                // ROLE_ADMIN 사용자만 답변 작성 가능
                // 회원은 403 Forbidden
                .authorizeHttpRequests(auth -> auth
                        // ===== Swagger =====
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ===== Notice =====
                        .requestMatchers(HttpMethod.GET, "/api/notice/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/notice").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/notice/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/notice/*").hasRole("ADMIN")

                        // ===== Inquiry =====
                        .requestMatchers(HttpMethod.POST, "/api/inquiry").authenticated() // 사용자 생성 가능
                        .requestMatchers(HttpMethod.GET, "/api/inquiry").authenticated()  // 사용자별 목록 조회
                        .requestMatchers(HttpMethod.GET, "/api/inquiry/*").authenticated() // 특정 문의 조회

                        // ===== Inquiry Answer =====
                        .requestMatchers(HttpMethod.POST, "/api/inquiry/*/answer").hasRole("ADMIN") // 답변 생성
                        .requestMatchers(HttpMethod.PUT, "/api/inquiry/*/answer").hasRole("ADMIN")  // 답변 수정
                        .requestMatchers(HttpMethod.DELETE, "/api/inquiry/*/answer").hasRole("ADMIN") // 답변 삭제
                        .requestMatchers(HttpMethod.GET, "/api/inquiry/*/answer").permitAll()         // 답변 조회

                        // ===== ETC =====
                        .anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}