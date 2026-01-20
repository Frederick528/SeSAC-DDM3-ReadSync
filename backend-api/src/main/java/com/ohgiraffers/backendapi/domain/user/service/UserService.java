package com.ohgiraffers.backendapi.domain.user.service;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.repository.RefreshTokenRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserInformationRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 1. 내 정보 조회
    public UserResponse.UserInfo getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // UserInformation은 없을 수도 있으니(혹시나) 예외처리 or 빈 객체
        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("상세 정보가 없습니다."));

        return UserResponse.UserInfo.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(userInfo.getNickname())
                .profileImage(userInfo.getProfileImage())
                .role(user.getRole().getKey())
                .provider(user.getProvider().name())
                .build();
    }

    // 2. 내 정보 수정 (닉네임 등)
    @Transactional
    public UserResponse.UserInfo updateProfile(Long userId, UserRequest.UpdateProfile request) {
        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("상세 정보가 없습니다."));

        // 닉네임 변경 요청이 있으면 변경
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {

            userInfo.updateNickname(request.getNickname());
        }

        if (request.getProfileImage() != null) {
            userInfo.updateProfileImage(request.getProfileImage());
        }

        return getMyProfile(userId);
    }

    @Transactional
    public void withdraw(Long userId) {

        refreshTokenRepository.deleteByUserId(userId);

        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("상세 정보가 없습니다."));
        userInfo.delete();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
        user.delete();
    }
}
