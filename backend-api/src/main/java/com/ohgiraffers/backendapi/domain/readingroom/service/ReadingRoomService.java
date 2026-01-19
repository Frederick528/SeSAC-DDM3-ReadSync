package com.ohgiraffers.backendapi.domain.readingroom.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadingRoomService {

    private final ReadingRoomRepository readingRoomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;

    // 독서룸 생성
    @Transactional
    public Long createRoom(Long hostId, CreateRoomRequest roomRequest) {
        User host = getUser(hostId);
        Library library = libraryRepository.findById(roomRequest.getLibraryId())
                .orElseThrow(() -> new CustomException(ErrorCode.LIBRARY_NOT_FOUND));

        if (readingRoomRepository.findByHost_IdAndStatusNot(hostId, RoomStatus.FINISHED).isPresent()) {
            throw new CustomException(ErrorCode.ROOM_ALREADY_EXISTS);
        }

        // 방제 지정
        String roomName = roomRequest.getRoomName();
        if (roomName == null || roomName.isBlank()) {
            roomName = library.getBook().getTitle() + " - " + library.getBook().getAuthor();
        }

        ReadingRoom room = ReadingRoom.builder()
                .host(host)
                .library(library)
                .roomName(roomName)
                .voiceType(roomRequest.getVoiceType())
                .maxCapacity(roomRequest.getMaxCapacity())
                .currentChapterId(roomRequest.getCurrentChapterId())
                .build();

        ReadingRoom savedRoom = readingRoomRepository.save(room);

        // 참여자 확인
        RoomParticipant hostParticipant = RoomParticipant.builder()
                .readingRoom(savedRoom)
                .user(host)
                .build();

        roomParticipantRepository.save(hostParticipant);

        return savedRoom.getRoomId();
    }



    // 방 입장
    @Transactional
    public void enterRoom(Long roomId, Long userId) {
        ReadingRoom room = getRoom(roomId);
        User user = getUser(userId);

        Optional<RoomParticipant> existingParticipant = roomParticipantRepository.findByReadingRoomAndUser(room, user);

        if (existingParticipant.isPresent()) {
            RoomParticipant participant = existingParticipant.get();
            // 재입장 여부 확인
            if (participant.isKicked()) {
                throw new CustomException(ErrorCode.KICKED_USER);
            }
            participant.reconnect();

        } else {
            // 방 상태 - 재생중 확인
            if (room.getStatus() == RoomStatus.PLAYING) {
                throw new CustomException(ErrorCode.ROOM_IS_PLAYING);
            }

            // 현재 인원 확인
            long currentCount = roomParticipantRepository.countByReadingRoomAndConnectionStatus(room, ConnectionStatus.ACTIVE);
            if (currentCount >= room.getMaxCapacity()) {
                throw new CustomException(ErrorCode.ROOM_IS_FULL);
            }

            RoomParticipant newParticipant = RoomParticipant.builder()
                    .readingRoom(room)
                    .user(user)
                    .build();
            roomParticipantRepository.save(newParticipant);
        }
    }



    // 방 퇴장
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        ReadingRoom room = getRoom(roomId);
        User user = getUser(userId);
        RoomParticipant participant = getParticipant(room, user);

        if (room.getHost().getId().equals(userId)) {
            room.finishRoom();
        } else {
            participant.leave();
        }

    }


    // 회원 강퇴
    @Transactional
    public void kickUser(Long roomId, Long hostId, Long targetUserId) {
        ReadingRoom room = getRoom(roomId);
        validateHost(room, hostId);

        User targetUser = getUser(targetUserId);
        RoomParticipant targetParticipant = getParticipant(room, targetUser);

        targetParticipant.kick();
    }


    // 재생속도 변경
    @Transactional
    public void updatePlaySpeed(Long roomId, Long hostId, BigDecimal speed) {
        ReadingRoom room = getRoom(roomId);
        validateHost(room, hostId);

        room.changePlaySpeed(speed);
    }

    // 재생 시작
    @Transactional
    public void startReading(Long roomId, Long hostId) {
        ReadingRoom room = getRoom(roomId);
        validateHost(room, hostId);

        room.updateStatus(RoomStatus.PLAYING);
    }

    // 목표 달성 및 종료
    @Transactional
    public void finishReading(Long roomId, Long hostId) {
        ReadingRoom room = getRoom(roomId);
        validateHost(room, hostId);

        // TODO: 경험치 지급 로직 (추후 구현)

        room.finishRoom();
    }

    // --- Private Helper Methods ---

    private ReadingRoom getRoom(Long roomId) {
        return readingRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private RoomParticipant getParticipant(ReadingRoom room, User user) {
        return roomParticipantRepository.findByReadingRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));
    }

    private void validateHost(ReadingRoom room, Long hostId) {
        if (!room.getHost().getId().equals(hostId)) {
            throw new CustomException(ErrorCode.NOT_HOST);
        }
    }
}
