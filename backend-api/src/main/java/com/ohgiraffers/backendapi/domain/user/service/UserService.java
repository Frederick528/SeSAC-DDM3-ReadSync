package com.ohgiraffers.backendapi.domain.user.service;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import com.ohgiraffers.backendapi.domain.user.repository.RefreshTokenRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserInformationRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 1. 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponse.UserInfo getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInformation userInfo = user.getUserInformation();

        // 데이터 무결성 체크 (유저는 있는데 상세 정보가 없는 경우 방지)
        if (userInfo == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return UserResponse.UserInfo.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(userInfo.getNickname())
                .tag(userInfo.getTag()) // 태그 포함
                .profileImage(userInfo.getProfileImage())
                .role(user.getRole().getKey())
                .provider(user.getProvider().name())
                .preferredGenre(userInfo.getPreferredGenre()) // 선호 장르 포함
                .build();
    }

    // 2. 내 정보 수정
    @Transactional
    public UserResponse.UserInfo updateProfile(Long userId, UserRequest.UpdateProfile request) {
        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // (1) 닉네임 변경 시 -> 태그도 새로 발급해서 중복 방지
        if (request.getNickname() != null && !request.getNickname().isEmpty()
                && !request.getNickname().equals(userInfo.getNickname())) {

            String newTag = generateUniqueTag(request.getNickname());
            userInfo.updateNicknameAndTag(request.getNickname(), newTag);
        }

        // (2) 프로필 이미지 변경
        if (request.getProfileImage() != null) {
            userInfo.updateProfileImage(request.getProfileImage());
        }

        // (3) 선호 장르 변경
        if (request.getPreferredGenre() != null && !request.getPreferredGenre().isEmpty()) {
            userInfo.updatePreferredGenre(request.getPreferredGenre());
        }

        // 수정된 최신 정보 반환
        return getMyProfile(userId);
    }

    // 3. 회원 탈퇴
    @Transactional
    public void withdraw(Long userId) {
        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // User 엔티티의 delete 호출 (Soft Delete 적용됨)
        user.delete();

        //  UserInformation도 삭제 처리할 경우 주석 해제
        // if (user.getUserInformation() != null) {
        //     user.getUserInformation().delete();
        // }
    }

    // 4. 타인 프로필 조회 (공개 정보만)
    @Transactional(readOnly = true)
    public UserResponse.OtherProfile getOtherProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 탈퇴하거나 정지된 유저는 조회 불가
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        UserInformation userInfo = user.getUserInformation();
        if (userInfo == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return UserResponse.OtherProfile.builder()
                .userId(user.getId())
                .nickname(userInfo.getNickname())
                .tag(userInfo.getTag()) // 동명이인 구분을 위해 태그 필수
                .profileImage(userInfo.getProfileImage())
                .build();
    }

    // 5. 유저 검색 (닉네임으로)
    @Transactional(readOnly = true)
    public List<UserResponse.OtherProfile> searchUsers(String keyword, Pageable pageable) {
        // 활성 유저(ACTIVE)만 검색
        Page<User> users = userRepository.findByNicknameAndStatus(keyword, UserStatus.ACTIVE, pageable);

        return users.stream()
                .map(user -> UserResponse.OtherProfile.builder()
                        .userId(user.getId())
                        .nickname(user.getUserInformation().getNickname())
                        .tag(user.getUserInformation().getTag())
                        .profileImage(user.getUserInformation().getProfileImage())
                        .build())
                .collect(Collectors.toList());
    }

    //  [관리자 전용 기능]

    // 6. 전체 회원 목록 조회
    @Transactional(readOnly = true)
    public Page<UserResponse.AdminUserDetail> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserResponse.AdminUserDetail.builder()
                        .userId(user.getId())
                        .loginId(user.getLoginId())
                        .nickname(user.getUserInformation() != null ? user.getUserInformation().getNickname() : "정보없음")
                        .tag(user.getUserInformation() != null ? user.getUserInformation().getTag() : "0000")
                        .role(user.getRole().getKey())
                        .status(user.getStatus().name())
                        .provider(user.getProvider().name())
                        .createdAt(user.getCreatedAt().toString())
                        .build());
    }

    // 7. 회원 상태 변경 (정지/해제)
    @Transactional
    public void changeStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관리자가 관리자를 정지시키는 것 방지
        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }

        user.updateStatus(status);
    }

    // 8. 특정 회원 상세 조회
    @Transactional(readOnly = true)
    public UserResponse.UserDetail getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInformation userInfo = user.getUserInformation();

        return UserResponse.UserDetail.from(user, userInfo);
    }

    // 태그 생성기 (중복 체크 포함)
    private String generateUniqueTag(String nickname) {
        String tag;
        do {
            int randomNum = new Random().nextInt(10000); // 0 ~ 9999
            tag = String.format("%04d", randomNum);      // 4자리로 포맷팅 (예: 0012)
        } while (userInformationRepository.existsByNicknameAndTag(nickname, tag));
        return tag;
    }
}