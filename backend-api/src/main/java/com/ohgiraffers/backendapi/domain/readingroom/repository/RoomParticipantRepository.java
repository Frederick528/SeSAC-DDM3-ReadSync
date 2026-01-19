package com.ohgiraffers.backendapi.domain.readingroom.repository;

import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.entity.RoomParticipant;
import com.ohgiraffers.backendapi.domain.readingroom.enums.ConnectionStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    // 특정 방에 특정 유저가 참여한 기록이 있는지 조회(강퇴 대상 찾기, 재입장 시도 시 기존 참여 이력(강퇴 여부 등) 확인)
    Optional<RoomParticipant> findByReadingRoomAndUser(ReadingRoom readingRoom, User user);

    // 방 인원 제한 체크
    long countByReadingRoomAndConnectionStatus(ReadingRoom readingRoom, ConnectionStatus connectionStatus);

    // 특정 방에 '접속 중(ACTIVE)'인 모든 참여자 목록 조회(방 종료 시 참여자들에게 경험치(EXP) 일괄 지급)
    List<RoomParticipant> findAllByReadingRoomAndConnectionStatus(ReadingRoom readingRoom, ConnectionStatus connectionStatus);
}
