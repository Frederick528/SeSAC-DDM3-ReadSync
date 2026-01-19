package com.ohgiraffers.backendapi.domain.readingroom.repository;

import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingRoomRepository extends JpaRepository<ReadingRoom, Long> {

    // 방장이 현재 운영 중인 방이 있는지 확인 (중복 방 생성 방지용)
    Optional<ReadingRoom> findByHost_IdAndStatusNot(Long hostId, RoomStatus status);
}
