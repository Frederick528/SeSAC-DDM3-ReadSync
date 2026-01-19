package com.ohgiraffers.backendapi.domain.readingroom.entity;

import com.ohgiraffers.backendapi.domain.readingroom.enums.InvitationStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "room_invitations")
public class RoomInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitations_id")
    private Long invitationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ReadingRoom readingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 발신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 수신자

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt; // 발송 시간

    @Builder
    public RoomInvitation(ReadingRoom readingRoom, User sender, User receiver) {
        this.readingRoom = readingRoom;
        this.sender = sender;
        this.receiver = receiver;
        this.status = InvitationStatus.PENDING;
    }

    // --- 비즈니스 로직 ---

    public void accept() { this.status = InvitationStatus.ACCEPTED; }
    public void reject() { this.status = InvitationStatus.REJECTED; }
    public void expire() { this.status = InvitationStatus.EXPIRED; }

    // 만료 체크 로직 (24시간)
    // TODO: 24시간은 너무 길어! 얼마나 줄이지?
    public boolean isExpired() {
        if (this.sentAt == null) return false; // 저장 전이라 시간이 없으면 만료 아님 처리
        return LocalDateTime.now().isAfter(this.sentAt.plusHours(24));
    }
}