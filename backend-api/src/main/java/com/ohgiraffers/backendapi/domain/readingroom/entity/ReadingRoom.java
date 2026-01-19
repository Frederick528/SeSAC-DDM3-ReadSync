package com.ohgiraffers.backendapi.domain.readingroom.entity;

import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.domain.readingroom.enums.RoomStatus;
import com.ohgiraffers.backendapi.domain.readingroom.enums.VoiceType;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "reading_rooms")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ReadingRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Column(name = "voice_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VoiceType voiceType = VoiceType.BASIC;

    @Column(name = "play_speed", nullable = false, precision = 3, scale = 1)
    @Builder.Default
    private BigDecimal playSpeed = BigDecimal.valueOf(1.0);

    @Column(name = "max_capacity", nullable = false)
    @Builder.Default
    private Integer maxCapacity = 8;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoomStatus status = RoomStatus.WAITING;

    // 왜 서재에서 안 받아오나!
    // 방장이 가진 책 중 임의의 책을 선택해서 읽는 것 따라서 library를 통해 내 책을 가져오지만 현재 독서룸에는 chapter가 연관관계를 맺을 필요 없음
    @Column(name = "current_chapter", nullable = false)
    private Integer currentChapterId;

    @Column(name = "last_read_pos", nullable = false)
    @Builder.Default
    private Integer lastReadPos = 0;


    // 상태 변경 메서드
    // 방 상태 변경
    public void updateStatus(RoomStatus newStatus) {
        this.status = newStatus;
    }

    // 재생 속도 변경
    public void changePlaySpeed(BigDecimal speed) {
        if (speed.doubleValue() < 0.5 || speed.doubleValue() > 2.0) {
            throw new CustomException(ErrorCode.INVALID_PLAY_SPEED);

        }
        this.playSpeed = speed;
    }

    // 현재 재생 위치 업데이트
    public void updateLastReadPos(Integer currentChapterId, Integer newPos) {
        this.currentChapterId = currentChapterId;
        this.lastReadPos = newPos;
    }

    // 방 종료
    public void finishRoom() {
        this.status = RoomStatus.FINISHED;
        this.delete();
    }
}
