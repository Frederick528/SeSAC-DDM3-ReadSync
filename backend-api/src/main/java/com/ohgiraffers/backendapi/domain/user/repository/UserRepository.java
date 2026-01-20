package com.ohgiraffers.backendapi.domain.user.repository;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 소셜 로그인 식별자(provider + providerId)로 유저 조회 (관리자용)
    Optional<User> findByProviderAndProviderId(SocialProvider provider, String providerId);

    Optional<User> findByLoginId(String loginId);

    Page<User> findByUserInformation_NicknameContainingAndStatus(String keyword, UserStatus status, Pageable pageable);

    // 1.  닉네임 포함 + 특정 상태(ACTIVE)인 유저만 조회
    @Query("SELECT u FROM User u JOIN u.userInformation ui " +
            "WHERE ui.nickname LIKE %:keyword% AND u.status = :status")
    Page<User> findByNicknameAndStatus(@Param("keyword") String keyword,
                                       @Param("status") UserStatus status,
                                       Pageable pageable);
}