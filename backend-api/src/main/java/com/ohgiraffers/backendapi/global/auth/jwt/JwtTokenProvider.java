package com.ohgiraffers.backendapi.global.auth.jwt;

import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
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
     * Refresh Token 생성 (Role 정보 불필요)
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, null, refreshTokenValidity);
    }

    private String createToken(Long userId, UserRole role, long validity) {
        Date now = new Date();
        Date validityTime = new Date(now.getTime() + validity);

        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId)) // sub: userId
                .issuedAt(now)                   // iat: 현재시간
                .expiration(validityTime)        // exp: 만료시간
                .signWith(key);                  // alg: HS256

        if (role != null) {
            builder.claim("role", role.name()); // role 정보 추가
        }

        return builder.compact();
    }

    /**
     * 토큰에서 userId 추출
     */
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
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

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}