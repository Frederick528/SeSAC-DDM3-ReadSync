package com.ohgiraffers.backendapi.domain.friendships.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.common.FriendshipsStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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

    @Builder
    public Friendships(User requester, User addressee, FriendshipsStatus status) {
        this.requester = requester;
        this.addressee = addressee;
        this.status = status;
    }
}
