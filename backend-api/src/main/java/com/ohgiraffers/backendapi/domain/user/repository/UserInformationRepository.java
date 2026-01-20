package com.ohgiraffers.backendapi.domain.user.repository;

import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {

    // user_id로 상세 정보 조회
    Optional<UserInformation> findByUserId(Long userId);

    boolean existsByNicknameAndTag(String nickname, String tag);
}