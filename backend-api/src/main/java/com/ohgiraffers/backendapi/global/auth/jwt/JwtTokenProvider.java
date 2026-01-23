package com.ohgiraffers.backendapi.global.auth.jwt;

import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // [수정] import 추가
import org.springframework.security.core.Authentication; // [수정] Tomcat -> Spring Security로 변경
import org.springframework.security.core.authority.SimpleGrantedAuthority; // [수정] import 추가
import org.springframework.security.core.userdetails.User; // [수정] Spring Security User 사용
import org.springframework.security.core.userdetails.UserDetails; // [수정] import 추가
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userId, UserRole role) {
        return createToken(userId, role, accessTokenValidity);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, null, refreshTokenValidity);
    }

    // 내부 토큰 생성 로직
    private String createToken(Long userId, UserRole role, long validity) {
        Date now = new Date();
        Date validityTime = new Date(now.getTime() + validity);

        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(validityTime)
                .signWith(key);

        if (role != null) {
            builder.claim("role", role.name());
        }

        return builder.compact();
    }

    /**
     * 토큰에서 userId 추출
     */
    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    /**
     * 토큰에서 인증 정보(Authentication) 조회
     * (SecurityContext에 저장할 객체 생성)
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // 클레임에서 권한 정보 가져오기 (없으면 기본 USER)
        String roleClaim = claims.get("role") != null ? claims.get("role").toString() : "USER";
        String role = roleClaim.startsWith("ROLE_") ? roleClaim : "ROLE_" + roleClaim;

        // UserDetails 객체 생성 (Spring Security의 User 객체 사용)
        UserDetails principal = new User(claims.getSubject(), "",
                Collections.singletonList(new SimpleGrantedAuthority(role)));

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    /**
     * 토큰 유효성 검증
     * (중복된 메서드를 하나로 통합했습니다)
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 내부 클레임 파싱 메서드 (중복 제거 및 통합)
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이어도 클레임은 반환 (필요 시)
            return e.getClaims();
        }
    }
}