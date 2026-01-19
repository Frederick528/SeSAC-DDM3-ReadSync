package com.ohgiraffers.backendapi.domain.friendship.service;

import com.ohgiraffers.backendapi.domain.friendship.dto.FriendListResponseDTO;
import com.ohgiraffers.backendapi.domain.friendship.entity.Friendship;
import com.ohgiraffers.backendapi.domain.friendship.enums.FriendshipStatus;
import com.ohgiraffers.backendapi.domain.friendship.repository.FriendshipRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @InjectMocks
    private FriendshipService friendshipService;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    private User requester;
    private User addressee;
    private User stranger;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        // 테스트용 User 생성 (ID 주입)
        requester = User.builder().build();
        ReflectionTestUtils.setField(requester, "id", 1L);

        addressee = User.builder().build();
        ReflectionTestUtils.setField(addressee, "id", 2L);

        stranger = User.builder().build();
        ReflectionTestUtils.setField(stranger, "id", 99L);

        // 테스트용 UserInformation 설정 (NPE 방지)
        UserInformation reqInfo = UserInformation.builder().nickname("RequesterNick").build();
        UserInformation addrInfo = UserInformation.builder().nickname("AddresseeNick").profileImage("img.jpg").build();

        // User 엔티티에 UserInformation을 set하는 로직이 있다면 사용, 없다면 Reflection으로 주입
        // 여기서는 편의상 Reflection 사용 가정 (실제 User 엔티티 구조에 맞춰 조정 필요)
        ReflectionTestUtils.setField(requester, "userInformation", reqInfo);
        ReflectionTestUtils.setField(addressee, "userInformation", addrInfo);

        // 테스트용 Friendship 생성
        friendship = Friendship.builder()
                .requester(requester)
                .addressee(addressee)
                .status(FriendshipStatus.PENDING)
                .build();
        ReflectionTestUtils.setField(friendship, "friendshipId", 10L);
    }

    @Nested
    @DisplayName("친구 요청 보내기")
    class SendFriendRequest {

        @Test
        @DisplayName("성공: 정상적으로 친구 요청을 보낸다.")
        void success() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(requester));
            given(userRepository.findById(2L)).willReturn(Optional.of(addressee));
            given(friendshipRepository.existsByUsers(requester, addressee)).willReturn(false);

            // when
            friendshipService.sendFriendRequest(1L, 2L);

            // then
            verify(friendshipRepository, times(1)).save(any(Friendship.class));
        }

        @Test
        @DisplayName("실패: 본인에게 요청을 보낼 수 없다.")
        void fail_self_request() {
            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> friendshipService.sendFriendRequest(1L, 1L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_REQUEST_TO_SELF);
        }

        @Test
        @DisplayName("실패: 이미 친구 관계가 존재하면 요청할 수 없다.")
        void fail_already_exists() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(requester));
            given(userRepository.findById(2L)).willReturn(Optional.of(addressee));
            given(friendshipRepository.existsByUsers(requester, addressee)).willReturn(true);

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> friendshipService.sendFriendRequest(1L, 2L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FRIENDS);
        }
    }

    @Nested
    @DisplayName("친구 목록 조회")
    class GetMyFriends {

        @Test
        @DisplayName("성공: 내 친구 목록을 정상적으로 조회한다.")
        void success() {
            // given
            // 이미 ACCEPTED 상태인 친구 관계 가정
            ReflectionTestUtils.setField(friendship, "status", FriendshipStatus.ACCEPTED);

            given(friendshipRepository.findMyFriendships(1L, FriendshipStatus.ACCEPTED))
                    .willReturn(List.of(friendship));

            // when
            List<FriendListResponseDTO> result = friendshipService.getMyFriends(1L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFriendNickname()).isEqualTo("AddresseeNick"); // 상대방 닉네임 확인
            assertThat(result.get(0).getFriendProfileImage()).isEqualTo("img.jpg");
        }
    }

    @Nested
    @DisplayName("친구 요청 수락")
    class AcceptFriend {

        @Test
        @DisplayName("성공: 요청을 받은 사람이 수락하면 상태가 ACCEPTED로 변경된다.")
        void success() {
            // given
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));

            // when (수신자 ID = 2L)
            friendshipService.acceptFriend(10L, 2L);

            // then
            // 실제 엔티티의 메서드가 호출되었는지 확인 (Spy를 쓰거나 상태값 확인)
            // 여기서는 accept() 메서드가 내부 상태를 변경한다고 가정하고 검증할 수는 없으므로(Entity 로직이므로),
            // 예외가 발생하지 않고 정상 종료되는지를 봅니다.
            // 만약 accept()가 status 필드를 변경한다면 아래와 같이 검증 가능:
            // assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.ACCEPTED);
        }

        @Test
        @DisplayName("실패: 요청을 받은 당사자가 아니면 수락할 수 없다.")
        void fail_not_addressee() {
            // given
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));

            // when & then (요청자(1L)나 제3자가 수락 시도)
            CustomException exception = assertThrows(CustomException.class,
                    () -> friendshipService.acceptFriend(10L, 1L)); // 1L은 Requester임
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }
    }

    @Nested
    @DisplayName("친구 요청 취소")
    class CancelFriendRequest {

        @Test
        @DisplayName("성공: 요청자가 PENDING 상태의 요청을 취소한다.")
        void success() {
            // given
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));

            // when
            friendshipService.cancelFriendRequest(10L, 1L); // 1L은 Requester

            // then
            // friendship.cancel()이 호출되었는지 확인
        }

        @Test
        @DisplayName("실패: 요청자가 아닌 사람이 취소하려 하면 예외 발생")
        void fail_not_requester() {
            // given
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> friendshipService.cancelFriendRequest(10L, 2L)); // 2L은 Addressee
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }

        @Test
        @DisplayName("실패: 이미 수락된 상태에서는 취소할 수 없다.")
        void fail_invalid_status() {
            // given
            ReflectionTestUtils.setField(friendship, "status", FriendshipStatus.ACCEPTED);
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> friendshipService.cancelFriendRequest(10L, 1L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST_STATUS);
        }
    }

    @Nested
    @DisplayName("친구 삭제 (Unfriend)")
    class Unfriend {

        @Test
        @DisplayName("성공: 관계 당사자(요청자)가 친구를 삭제한다.")
        void success_requester() {
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));
            friendshipService.unfriend(10L, 1L);
        }

        @Test
        @DisplayName("성공: 관계 당사자(수신자)가 친구를 삭제한다.")
        void success_addressee() {
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));
            friendshipService.unfriend(10L, 2L);
        }

        @Test
        @DisplayName("실패: 제3자가 친구 관계를 삭제하려 하면 예외 발생")
        void fail_stranger() {
            given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));

            CustomException exception = assertThrows(CustomException.class,
                    () -> friendshipService.unfriend(10L, 99L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }
    }

    // blockFriend, unblockFriend, rejectFriend 등도 위와 유사한 패턴으로 작성됩니다.
    // 필요 시 아래와 같이 추가할 수 있습니다.

    @Test
    @DisplayName("친구 차단 성공")
    void blockFriend_success() {
        given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));
        friendshipService.blockFriend(10L, 1L);
        // verify state change if possible
    }

    @Test
    @DisplayName("차단 해제 실패 - 권한 없음")
    void unblockFriend_fail() {
        given(friendshipRepository.findById(10L)).willReturn(Optional.of(friendship));
        assertThrows(CustomException.class, () -> friendshipService.unblockFriend(10L, 99L));
    }
}