package com.ohgiraffers.backendapi.domain.readingroom.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.library.repository.LibraryRepository;
import com.ohgiraffers.backendapi.domain.readingroom.dto.CreateRoomRequest;
import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.entity.RoomParticipant;
import com.ohgiraffers.backendapi.domain.readingroom.enums.ConnectionStatus;
import com.ohgiraffers.backendapi.domain.readingroom.enums.RoomStatus;
import com.ohgiraffers.backendapi.domain.readingroom.repository.ReadingRoomRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingRoomServiceTest {

    @InjectMocks
    private ReadingRoomService readingRoomService;

    @Mock
    private ReadingRoomRepository readingRoomRepository;
    @Mock
    private RoomParticipantRepository roomParticipantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LibraryRepository libraryRepository;

    // 테스트용 상수
    private final Long HOST_ID = 1L;
    private final Long MEMBER_ID = 2L;
    private final Long ROOM_ID = 10L;

    @Nested
    @DisplayName("독서룸 생성 (createRoom)")
    class CreateRoomTest {

        @Test
        @DisplayName("성공: 유효한 요청시 방을 생성하고 방장을 참여자로 등록한다")
        void success() {
            // given
            CreateRoomRequest request = new CreateRoomRequest();
            // Reflection or Setter를 통해 DTO 값 주입 (여기선 가정)
            // request.setLibraryId(LIBRARY_ID); ...

            // Mocking Request Data (직접 객체 생성 가정)
            given(libraryRepository.findById(any())).willReturn(Optional.of(mock(Library.class)));

            // Library & Book Mocking for title generation
            Library library = mock(Library.class);
            Book book = mock(Book.class);
            given(libraryRepository.findById(any())).willReturn(Optional.of(library));
            given(library.getBook()).willReturn(book);
            given(book.getTitle()).willReturn("Test Book");
            given(book.getAuthor()).willReturn("Author");

            User host = mock(User.class);
            given(userRepository.findById(HOST_ID)).willReturn(Optional.of(host));

            // 중복 방 없음
            given(readingRoomRepository.findByHost_IdAndStatusNot(HOST_ID, RoomStatus.FINISHED))
                    .willReturn(Optional.empty());

            // save 호출 시 id가 있는 객체 반환 흉내
            ReadingRoom savedRoom = mock(ReadingRoom.class);
            given(savedRoom.getRoomId()).willReturn(ROOM_ID);
            given(readingRoomRepository.save(any(ReadingRoom.class))).willReturn(savedRoom);

            // when
            Long resultId = readingRoomService.createRoom(HOST_ID, request);

            // then
            assertThat(resultId).isEqualTo(ROOM_ID);
            verify(readingRoomRepository, times(1)).save(any(ReadingRoom.class));
            verify(roomParticipantRepository, times(1)).save(any(RoomParticipant.class)); // 방장 참여 저장 확인
        }

        @Test
        @DisplayName("실패: 이미 운영 중인 방이 있으면 예외 발생")
        void fail_duplicate_room() {
            // given
            CreateRoomRequest request = new CreateRoomRequest();
            User host = mock(User.class);
            given(userRepository.findById(HOST_ID)).willReturn(Optional.of(host));
            given(libraryRepository.findById(any())).willReturn(Optional.of(mock(Library.class)));

            // 이미 방이 존재함
            given(readingRoomRepository.findByHost_IdAndStatusNot(HOST_ID, RoomStatus.FINISHED))
                    .willReturn(Optional.of(mock(ReadingRoom.class)));

            // when & then
            assertThatThrownBy(() -> readingRoomService.createRoom(HOST_ID, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROOM_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("방 입장 (enterRoom)")
    class EnterRoomTest {

        @Test
        @DisplayName("성공: 신규 회원이 입장 조건을 만족하면 참여자로 저장된다")
        void success_new_member() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User user = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(user));

            // 기존 참여 기록 없음
            given(roomParticipantRepository.findByReadingRoomAndUser(room, user)).willReturn(Optional.empty());

            // 방 상태 정상, 인원 미달
            given(room.getStatus()).willReturn(RoomStatus.WAITING);
            given(room.getMaxCapacity()).willReturn(8);
            given(roomParticipantRepository.countByReadingRoomAndConnectionStatus(room, ConnectionStatus.ACTIVE)).willReturn(5L);

            // when
            readingRoomService.enterRoom(ROOM_ID, MEMBER_ID);

            // then
            verify(roomParticipantRepository, times(1)).save(any(RoomParticipant.class));
        }

        @Test
        @DisplayName("성공: 기존 회원이 재입장하면 reconnect()가 호출된다")
        void success_reconnect() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User user = mock(User.class);
            RoomParticipant participant = mock(RoomParticipant.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(user));

            // 기존 참여 기록 있음
            given(roomParticipantRepository.findByReadingRoomAndUser(room, user)).willReturn(Optional.of(participant));
            given(participant.isKicked()).willReturn(false); // 강퇴 안 당함

            // when
            readingRoomService.enterRoom(ROOM_ID, MEMBER_ID);

            // then
            verify(participant, times(1)).reconnect(); // reconnect 호출 확인
            verify(roomParticipantRepository, never()).save(any(RoomParticipant.class)); // 저장은 안 함
        }

        @Test
        @DisplayName("실패: 강퇴당한 회원은 재입장 불가")
        void fail_kicked_user() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User user = mock(User.class);
            RoomParticipant participant = mock(RoomParticipant.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(user));
            given(roomParticipantRepository.findByReadingRoomAndUser(room, user)).willReturn(Optional.of(participant));

            given(participant.isKicked()).willReturn(true); // 강퇴 당함

            // when & then
            assertThatThrownBy(() -> readingRoomService.enterRoom(ROOM_ID, MEMBER_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.KICKED_USER);
        }

        @Test
        @DisplayName("실패: 재생 중(PLAYING)인 방에 신규 입장은 불가")
        void fail_room_playing() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User user = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(user));
            given(roomParticipantRepository.findByReadingRoomAndUser(room, user)).willReturn(Optional.empty());

            given(room.getStatus()).willReturn(RoomStatus.PLAYING); // 재생 중

            // when & then
            assertThatThrownBy(() -> readingRoomService.enterRoom(ROOM_ID, MEMBER_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROOM_IS_PLAYING);
        }

        @Test
        @DisplayName("실패: 인원이 가득 찬 방은 입장 불가")
        void fail_room_full() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User user = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(user));
            given(roomParticipantRepository.findByReadingRoomAndUser(room, user)).willReturn(Optional.empty());

            given(room.getStatus()).willReturn(RoomStatus.WAITING);
            given(room.getMaxCapacity()).willReturn(8);
            given(roomParticipantRepository.countByReadingRoomAndConnectionStatus(room, ConnectionStatus.ACTIVE)).willReturn(8L); // 꽉 참

            // when & then
            assertThatThrownBy(() -> readingRoomService.enterRoom(ROOM_ID, MEMBER_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROOM_IS_FULL);
        }
    }

    @Nested
    @DisplayName("방 퇴장 (leaveRoom)")
    class LeaveRoomTest {

        @Test
        @DisplayName("성공: 일반 회원이 나가면 leave()가 호출된다")
        void success_member_leave() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User user = mock(User.class);
            User host = mock(User.class);
            RoomParticipant participant = mock(RoomParticipant.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(user));
            given(roomParticipantRepository.findByReadingRoomAndUser(room, user)).willReturn(Optional.of(participant));

            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);

            // when
            readingRoomService.leaveRoom(ROOM_ID, MEMBER_ID);

            // then
            verify(participant, times(1)).leave();
            verify(room, never()).finishRoom();
        }

        @Test
        @DisplayName("성공: 방장이 나가면 finishRoom()이 호출된다")
        void success_host_leave() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User host = mock(User.class);
            RoomParticipant participant = mock(RoomParticipant.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(userRepository.findById(HOST_ID)).willReturn(Optional.of(host));
            given(roomParticipantRepository.findByReadingRoomAndUser(room, host)).willReturn(Optional.of(participant));

            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID); // 방장 본인

            // when
            readingRoomService.leaveRoom(ROOM_ID, HOST_ID);

            // then
            verify(room, times(1)).finishRoom();
        }
    }

    @Nested
    @DisplayName("기타 기능 테스트")
    class OtherFeaturesTest {

        @Test
        @DisplayName("강퇴: 방장이 회원을 강퇴하면 kick()이 호출된다")
        void kickUser() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User host = mock(User.class);
            User target = mock(User.class);
            RoomParticipant targetParticipant = mock(RoomParticipant.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID); // 방장 확인

            given(userRepository.findById(MEMBER_ID)).willReturn(Optional.of(target));
            given(roomParticipantRepository.findByReadingRoomAndUser(room, target)).willReturn(Optional.of(targetParticipant));

            // when
            readingRoomService.kickUser(ROOM_ID, HOST_ID, MEMBER_ID);

            // then
            verify(targetParticipant, times(1)).kick();
        }

        @Test
        @DisplayName("강퇴 실패: 방장이 아니면 권한 없음 예외")
        void kickUser_fail_not_host() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User realHost = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(realHost);
            given(realHost.getId()).willReturn(HOST_ID);

            // when & then (MEMBER_ID가 강퇴 시도)
            assertThatThrownBy(() -> readingRoomService.kickUser(ROOM_ID, MEMBER_ID, 3L))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_HOST);
        }

        @Test
        @DisplayName("재생 속도 변경: 방장이면 속도를 변경한다")
        void updatePlaySpeed() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User host = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);

            BigDecimal newSpeed = BigDecimal.valueOf(1.5);

            // when
            readingRoomService.updatePlaySpeed(ROOM_ID, HOST_ID, newSpeed);

            // then
            verify(room, times(1)).changePlaySpeed(newSpeed);
        }

        @Test
        @DisplayName("재생 시작: 방장이면 상태를 변경한다")
        void startReading() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User host = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);

            // when
            readingRoomService.startReading(ROOM_ID, HOST_ID);

            // then
            verify(room, times(1)).updateStatus(RoomStatus.PLAYING);
        }

        @Test
        @DisplayName("목표 달성: 방장이면 방을 종료(finishRoom)한다")
        void finishReading() {
            // given
            ReadingRoom room = mock(ReadingRoom.class);
            User host = mock(User.class);

            given(readingRoomRepository.findById(ROOM_ID)).willReturn(Optional.of(room));
            given(room.getHost()).willReturn(host);
            given(host.getId()).willReturn(HOST_ID);

            // when
            readingRoomService.finishReading(ROOM_ID, HOST_ID);

            // then
            verify(room, times(1)).finishRoom();
        }
    }
}