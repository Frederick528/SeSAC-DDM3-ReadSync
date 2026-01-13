package com.ohgiraffers.backendapi.domain.friendships.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.domain.friendships.enums.FriendshipsStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "friendships",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_FRIENDSHIP_PAIR", columnNames = {"requester_id", "addressee_id"})
        }
)
public class Friendships extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "following_id")
    private Long friendshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressee_id", nullable = false)
    private User addressee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipsStatus status;


    // 상태 변경 편의 메서드
    /**
     * 친구 요청 수락
     */
    public void accept() {
        this.status = FriendshipsStatus.ACCEPTED;
    }

    /**
     * 친구 요청 거절
     */
    public void reject() {
        this.status = FriendshipsStatus.REJECTED;
    }

    /**
     *  상대방 차단
     */
    public void block() {
        this.status = FriendshipsStatus.BLOCKED;
    }

    /**
     * 친구 삭제
     */
    public void unfriend() {
        this.delete();
    }

    /**
     * 차단 해제
     */
    public void unblockFriendships() {
        this.delete();
    }
}
