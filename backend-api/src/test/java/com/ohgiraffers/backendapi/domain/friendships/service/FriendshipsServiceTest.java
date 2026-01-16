package com.ohgiraffers.backendapi.domain.friendships.service;

import com.ohgiraffers.backendapi.domain.friendships.entity.Friendships;
import com.ohgiraffers.backendapi.domain.friendships.enums.FriendshipsStatus;
import com.ohgiraffers.backendapi.domain.friendships.repository.FriendshipsRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FriendshipsServiceTest {

    @InjectMocks
    private FriendshipsService friendshipsService;

    @Mock
    private FriendshipsRepository friendshipsRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("친구 요청 성공")
    void sendFriendRequestSuccess() {
        // given
        Long requesterId = 1L;
        Long addresseeId = 2L;

        User requester = User.builder().id(1L).build();
        User addressee = User.builder().id(2L).build();

        given(userRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(userRepository.findById(addresseeId)).willReturn(Optional.of(addressee));

        // 이미 친구인 경우 false 반환
        given(friendshipsRepository.existsByUsers(requester, addressee)).willReturn(false);

        // when (실행)
        friendshipsService.sendFriendRequest(requesterId, addresseeId);

        // then (검증)
        verify(friendshipsRepository).save(any(Friendships.class));
    }

    @Test
    @DisplayName("친구 요청 실패: 이미 친구인 경우 에러 발생")
    void sendFriendRequest_Fail_AlreadyFriend() {
        // given
        Long requesterId = 1L;
        Long addresseeId = 2L;
        User requester = User.builder().id(1L).build();
        User addressee = User.builder().id(2L).build();

        given(userRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(userRepository.findById(addresseeId)).willReturn(Optional.of(addressee));

        // 이미 친구인 경우 true 반환
        given(friendshipsRepository.existsByUsers(requester, addressee)).willReturn(true);

        // when & then (실행 및 에러 검증)
        assertThatThrownBy(() -> friendshipsService.sendFriendRequest(requesterId, addresseeId))
                .isInstanceOf(CustomException.class) // 에러가 발생해야 함
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_FRIENDS); // 에러 코드 확인
    }

    @Test
    @DisplayName("친구 요청 취소 실패: 요청자 본인이 아니면 취소 못 함")
    void cancelFriendRequest_Fail_NotOwner() {
        // given
        Long myId = 1L;
        Long otherId = 2L;

        // 상황: 2번(otherId)이 요청한 건을 1번(myId)이 취소하려 함
        User requester = User.builder().id(otherId).build();

        Friendships friendship = Friendships.builder()
                .friendshipId(100L)
                .requester(requester)
                .status(FriendshipsStatus.PENDING)
                .build();

        given(friendshipsRepository.findById(100L)).willReturn(Optional.of(friendship));

        // when & then
        assertThatThrownBy(() -> friendshipsService.cancelFriendRequest(100L, myId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NO_AUTHORITY_TO_UPDATE);
    }
}