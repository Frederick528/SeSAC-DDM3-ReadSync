package com.ohgiraffers.backendapi.domain.user.repository;

import com.ohgiraffers.backendapi.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}