package com.ohgiraffers.backendapi.domain.user.repository;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 소셜 로그인 식별자(provider + providerId)로 유저 조회
    Optional<User> findByProviderAndProviderId(SocialProvider provider, String providerId);

    Optional<User> findByLoginId(String loginId);
}