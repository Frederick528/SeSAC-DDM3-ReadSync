package com.ohgiraffers.backendapi.domain.friendship.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.domain.friendship.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.ohgiraffers.backendapi.domain.user.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Table(
        name = "friendships",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_FRIENDSHIP_PAIR", columnNames = {"requester_id", "addressee_id"})
        }
)
public class Friendship extends BaseTimeEntity {

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
    @Builder.Default
    private FriendshipStatus status = FriendshipStatus.PENDING;


    // 상태 변경 편의 메서드
    /**
     * 친구 요청 수락
     */
    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
    }

    /**
     * 친구 요청 거절
     */
    public void reject() {
        this.status = FriendshipStatus.REJECTED;
    }

    /**
     *  상대방 차단
     */
    public void block() {
        this.status = FriendshipStatus.BLOCKED;
    }

    /**
     * 친구 삭제
     */
    public void unfriend() {
        this.delete();
    }

    /**
     * 차단 해제: 로직상 차단을 풀면 '남남'이 되야 하므로 delete() 호출
     */
    public void unblockFriendships() {
        this.delete();
    }

    /**
     * 친구 요청 취소
     */
    public void cancel() {
        this.delete();
    }
}
