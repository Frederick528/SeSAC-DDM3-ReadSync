package com.ohgiraffers.backendapi.domain.readingroom.repository;

import com.ohgiraffers.backendapi.domain.readingroom.entity.ReadingRoom;
import com.ohgiraffers.backendapi.domain.readingroom.enums.RoomStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingRoomRepository extends JpaRepository<ReadingRoom, Long> {

    // [추가] 락을 걸고 방 정보 조회 (비관적 락)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReadingRoom r WHERE r.roomId = :roomId")
    Optional<ReadingRoom> findByIdWithLock(@Param("roomId") Long roomId);

    // 방장이 현재 운영 중인 방이 있는지 확인 (중복 방 생성 방지용)
    Optional<ReadingRoom> findByHost_IdAndStatusNot(Long hostId, RoomStatus status);
}
