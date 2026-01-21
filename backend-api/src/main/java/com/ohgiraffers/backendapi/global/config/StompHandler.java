package com.ohgiraffers.backendapi.global.config;

import com.ohgiraffers.backendapi.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)     // 최우선순위 설정
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 연결 요청(CONNECT)일 때만 토큰 검사
        if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {

            // 토큰 추출
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.error("WS Connect Fail: No JWT Token found");
                throw new AccessDeniedException("로그인이 필요합니다.");
            }

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                if (jwtTokenProvider.validateToken(token)) {
                    // 인증 객체 생성
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);

                    // 소켓 세션 등록
                    accessor.setUser(authentication);

                    log.info("WS Connect Success: User Id = {}", authentication.getName());

                } else {
                    log.warn("WS Connect Fail: Invalid Token");
                    throw new AccessDeniedException("유효하지 않은 토큰입니다.");
                }
            } else  {
                log.warn("WS Connect Fail: No Token");
                throw new AccessDeniedException("토큰이 없습니다.");
            }
        }

        return message;
    }
}
