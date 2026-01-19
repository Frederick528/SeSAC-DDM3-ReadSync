package com.ohgiraffers.backendapi.domain.readingroom.repository;

import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.entity.RoomInvitation;
import com.ohgiraffers.backendapi.domain.readingroom.enums.InvitationStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomInvitationRepository extends JpaRepository<RoomInvitation, Long> {

    // 내가 받은 초대장 중 '대기 중(PENDING)'인 목록 조회
    List<RoomInvitation> findByReceiverAndStatus(User receiver, InvitationStatus status);

    // 중복 초대 방지 (이미 초대했는데 또 보내는 것 막기)
    Optional<RoomInvitation> findByReadingRoomAndReceiverAndStatus(ReadingRoom readingRoom, User receiver, InvitationStatus status);
}
