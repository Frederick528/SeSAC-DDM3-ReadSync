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
import java.util.Random; // ★ Random import 필수!
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

        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.UserInfo.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(userInfo.getNickname())
                .tag(userInfo.getTag())
                .profileImage(userInfo.getProfileImage())
                .role(user.getRole().getKey())
                .provider(user.getProvider().name())
                .build();
    }

    // 2. 내 정보 수정 (★ 핵심 수정 부분)
    @Transactional
    public UserResponse.UserInfo updateProfile(Long userId, UserRequest.UpdateProfile request) {
        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 변경 요청이 있고, 기존 닉네임과 다를 경우에만 로직 실행
        if (request.getNickname() != null && !request.getNickname().isEmpty()
                && !request.getNickname().equals(userInfo.getNickname())) {

            // 1. 새 닉네임에 맞는 유니크한 태그 생성
            String newTag = generateUniqueTag(request.getNickname());

            // 2. 닉네임과 태그 동시 변경 (Entity에 메서드 추가 필요)
            userInfo.updateNicknameAndTag(request.getNickname(), newTag);
        }

        if (request.getProfileImage() != null) {
            userInfo.updateProfileImage(request.getProfileImage());
        }

        // 변경된 정보로 다시 조회해서 반환
        return getMyProfile(userId);
    }

    // 3. 회원 탈퇴
    @Transactional
    public void withdraw(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

        // 정보를 삭제할건지 고민 해봐야함
        // UserInformation userInfo = userInformationRepository.findByUserId(userId)
        //        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
       // userInfo.delete();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.delete();
    }

    // 4. 타인 프로필 조회
    @Transactional(readOnly = true)
    public UserResponse.OtherProfile getOtherProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
                .tag(userInfo.getTag())
                .profileImage(userInfo.getProfileImage())
                .build();
    }

    // 5. 유저 검색
    @Transactional(readOnly = true)
    public List<UserResponse.OtherProfile> searchUsers(String keyword, Pageable pageable) {
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

    // 6. [관리자] 전체 회원 목록 조회
    @Transactional(readOnly = true)
    public Page<UserResponse.AdminUserDetail> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserResponse.AdminUserDetail.builder()
                        .userId(user.getId())
                        .loginId(user.getLoginId())
                        .nickname(user.getUserInformation() != null ? user.getUserInformation().getNickname() : "정보없음")
                        .role(user.getRole().getKey())
                        .status(user.getStatus().name())
                        .provider(user.getProvider().name())
                        .createdAt(user.getCreatedAt().toString())
                        .tag(user.getUserInformation().getTag())
                        .build());
    }

    // 7. [관리자] 회원 상태 변경
    @Transactional
    public void changeStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE); // ErrorCode 확인
        }

        user.updateStatus(status);
    }

    // 8. [관리자] 상세 조회
    @Transactional(readOnly = true)
    public UserResponse.UserDetail getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInformation userInfo = user.getUserInformation();

        return UserResponse.UserDetail.from(user, userInfo); // UserDetail.from 메서드 안에서도 tag를 넣어야 함
    }

    // ★ [핵심] 태그 생성 메서드 (AuthService와 동일 로직)
    private String generateUniqueTag(String nickname) {
        String tag;
        do {
            int randomNum = new Random().nextInt(10000);
            tag = String.format("%04d", randomNum);
        } while (userInformationRepository.existsByNicknameAndTag(nickname, tag));
        return tag;
    }
}