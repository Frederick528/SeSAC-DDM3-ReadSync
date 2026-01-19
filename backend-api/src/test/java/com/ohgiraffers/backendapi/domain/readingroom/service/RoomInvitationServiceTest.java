package com.ohgiraffers.backendapi.domain.readingroom.service;

import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.entity.RoomInvitation;
import com.ohgiraffers.backendapi.domain.readingroom.enums.ConnectionStatus;
import com.ohgiraffers.backendapi.domain.readingroom.enums.InvitationStatus;
import com.ohgiraffers.backendapi.domain.readingroom.enums.RoomStatus;
import com.ohgiraffers.backendapi.domain.readingroom.repository.ReadingRoomRepository;
import com.ohgiraffers.backendapi.domain.readingroom.repository.RoomInvitationRepository;
import com.ohgiraffers.backendapi.domain.readingroom.repository.RoomParticipantRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용 설정
class RoomInvitationServiceTest {

    @InjectMocks
    private RoomInvitationService invitationService;

    @Mock
    private RoomInvitationRepository invitationRepository;
    @Mock
    private ReadingRoomRepository readingRoomRepository;
    @Mock
    private RoomParticipantRepository roomParticipantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReadingRoomService readingRoomService; // 순환 참조 문제 해결 (Mock으로 주입)

    // 테스트용 더미 ID
    private final Long HOST_ID = 1L;
    private final Long TARGET_ID = 2L;
    private final Long OTHER_ID = 3L;
    private final Long ROOM_ID = 10L;
    private final Long INVITATION_ID = 100L;

    @Nested
    @DisplayName("초대장 발송 (inviteUser)")
    class InviteUserTest {

        @Test
        @DisplayName("성공: 모든 조건이 충족되면 초대장이 저장된다")
        void success() {
            // given
            User host = mock(User.class);
            User receiver = mock(User.class);
            ReadingRoom room = mock(ReadingRoom.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);

            // 상태 체크 통과 설정
            given(room.getStatus()).willReturn(RoomStatus.WAITING);
            given(room.getMaxCapacity()).willReturn(8);
            given(roomParticipantRepository.countByReadingRoomAndConnectionStatus(room, ConnectionStatus.ACTIVE)).willReturn(5L);

            given(userRepository.findById(TARGET_ID)).willReturn(Optional.of(receiver));
            given(invitationRepository.findByReadingRoomAndReceiverAndStatus(room, receiver, InvitationStatus.PENDING))
                    .willReturn(Optional.empty()); // 중복 초대 없음

            // when
            invitationService.inviteUser(ROOM_ID, HOST_ID, TARGET_ID);

            // then
            verify(invitationRepository, times(1)).save(any(RoomInvitation.class));
        }

        @Test
        @DisplayName("실패: 방장이 아닌 경우 예외 발생")
        void fail_not_host() {
            // given
            User host = mock(User.class);
            ReadingRoom room = mock(ReadingRoom.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(OTHER_ID); // 방장 ID 다름

            // when & then
            assertThatThrownBy(() -> invitationService.inviteUser(ROOM_ID, HOST_ID, TARGET_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_HOST);
        }

        @Test
        @DisplayName("실패: 방이 재생 중(PLAYING)인 경우 예외 발생")
        void fail_room_playing() {
            // given
            User host = mock(User.class);
            ReadingRoom room = mock(ReadingRoom.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);
            given(room.getStatus()).willReturn(RoomStatus.PLAYING); // 재생 중 설정

            // when & then
            assertThatThrownBy(() -> invitationService.inviteUser(ROOM_ID, HOST_ID, TARGET_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVITATION_NOT_ALLOWED_PLAYING);
        }

        @Test
        @DisplayName("실패: 이미 초대된 사용자일 경우 예외 발생")
        void fail_already_invited() {
            // given
            User host = mock(User.class);
            User receiver = mock(User.class);
            ReadingRoom room = mock(ReadingRoom.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);
            given(room.getStatus()).willReturn(RoomStatus.WAITING);
            given(room.getMaxCapacity()).willReturn(8);
            given(roomParticipantRepository.countByReadingRoomAndConnectionStatus(room, ConnectionStatus.ACTIVE)).willReturn(1L);

            given(userRepository.findById(TARGET_ID)).willReturn(Optional.of(receiver));

            // 이미 PENDING 상태인 초대장이 존재한다고 설정
            given(invitationRepository.findByReadingRoomAndReceiverAndStatus(room, receiver, InvitationStatus.PENDING))
                    .willReturn(Optional.of(mock(RoomInvitation.class)));

            // when & then
            assertThatThrownBy(() -> invitationService.inviteUser(ROOM_ID, HOST_ID, TARGET_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_INVITED);
        }
    }

    @Nested
    @DisplayName("초대장 수락 (acceptInvitation)")
    class AcceptInvitationTest {

        @Test
        @DisplayName("성공: 초대장을 수락하면 상태가 변경되고 방에 입장한다")
        void success() {
            // given
            RoomInvitation invitation = mock(RoomInvitation.class);
            ReadingRoom room = mock(ReadingRoom.class);
            User receiver = mock(User.class);

            given(invitationRepository.findById(INVITATION_ID)).willReturn(Optional.of(invitation));
            given(invitation.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(TARGET_ID); // 본인 확인 통과
            given(invitation.getStatus()).willReturn(InvitationStatus.PENDING); // 만료 안 됨

            given(invitation.getReadingRoom()).willReturn(room);
            given(room.getRoomId()).willReturn(ROOM_ID);

            // when
            invitationService.acceptInvitation(INVITATION_ID, TARGET_ID);

            // then
            verify(invitation, times(1)).accept(); // 상태 변경 메서드 호출 확인
            verify(readingRoomService, times(1)).enterRoom(ROOM_ID, TARGET_ID); // 입장 메서드 호출 확인
        }

        @Test
        @DisplayName("실패: 본인의 초대장이 아닌 경우 예외 발생")
        void fail_not_your_invitation() {
            // given
            RoomInvitation invitation = mock(RoomInvitation.class);
            User receiver = mock(User.class);

            given(invitationRepository.findById(INVITATION_ID)).willReturn(Optional.of(invitation));
            given(invitation.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(OTHER_ID); // 다른 사람 ID

            // when & then
            assertThatThrownBy(() -> invitationService.acceptInvitation(INVITATION_ID, TARGET_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_YOUR_INVITATION);
        }

        @Test
        @DisplayName("실패: 초대장이 만료되었거나 PENDING 상태가 아닌 경우 예외 발생")
        void fail_invitation_expired() {
            // given
            RoomInvitation invitation = mock(RoomInvitation.class);
            User receiver = mock(User.class);

            given(invitationRepository.findById(INVITATION_ID)).willReturn(Optional.of(invitation));
            given(invitation.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(TARGET_ID);
            given(invitation.getStatus()).willReturn(InvitationStatus.EXPIRED); // 이미 만료됨

            // when & then
            assertThatThrownBy(() -> invitationService.acceptInvitation(INVITATION_ID, TARGET_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVITATION_EXPIRED);
        }
    }

    @Nested
    @DisplayName("초대장 거절 (rejectInvitation)")
    class RejectInvitationTest {

        @Test
        @DisplayName("성공: 초대장을 거절하면 상태가 변경된다")
        void success() {
            // given
            RoomInvitation invitation = mock(RoomInvitation.class);
            User receiver = mock(User.class);

            given(invitationRepository.findById(INVITATION_ID)).willReturn(Optional.of(invitation));
            given(invitation.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(TARGET_ID);

            // when
            invitationService.rejectInvitation(INVITATION_ID, TARGET_ID);

            // then
            verify(invitation, times(1)).reject(); // 거절 메서드 호출 확인
        }
    }
}