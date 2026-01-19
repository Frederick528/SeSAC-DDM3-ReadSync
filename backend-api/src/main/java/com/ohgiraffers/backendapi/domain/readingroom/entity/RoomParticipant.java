package com.ohgiraffers.backendapi.domain.readingroom.entity;

import com.ohgiraffers.backendapi.domain.readingroom.enums.ConnectionStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (created_at 자동 주입)
@Table(name = "room_participants")
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participants_id")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ReadingRoom readingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 입장시간

    @Column(name = "is_kicked", nullable = false)
    private boolean isKicked;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false, length = 50)
    private ConnectionStatus connectionStatus;

    @Builder
    public RoomParticipant(ReadingRoom readingRoom, User user) {
        this.readingRoom = readingRoom;
        this.user = user;
        this.isKicked = false;
        this.connectionStatus = ConnectionStatus.ACTIVE;
    }

    // --- 비즈니스 로직 ---

    public void kick() {
        this.isKicked = true;
        this.connectionStatus = ConnectionStatus.EXITED;
    }

    public void leave() {
        this.connectionStatus = ConnectionStatus.EXITED;
    }

    public void disconnect() {
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
    }

    public void reconnect() {
        if (this.isKicked) {
            throw new IllegalStateException("강퇴당한 사용자는 재입장할 수 없습니다.");
        }
        this.connectionStatus = ConnectionStatus.ACTIVE;
    }
}