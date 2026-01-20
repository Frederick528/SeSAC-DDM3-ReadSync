package com.ohgiraffers.backendapi.domain.friendship.repository;

import com.ohgiraffers.backendapi.domain.friendship.entity.Friendship;
import com.ohgiraffers.backendapi.domain.friendship.enums.FriendshipStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // 중복 요청 방지
    @Query("select case when count(f) > 0 then true else false end " +
            "from Friendship f " +
            "where f.requester = :userA and f.addressee = :userB " +
            "or (f.requester = :userB and f.addressee = :userA)")
    boolean existsByUsers(@Param("userA") User userA, @Param("userB") User userB);

    // 특정 관계 조회(삭제나 상태 변경시)
    Optional<Friendship> findByRequesterAndAddressee(User requester, User addressee);

    // 내 친구 목록 조회
    @Query("select f from Friendship f " +
            "join fetch f.requester r " +
            "left join fetch r.userInformation " +
            "join fetch f.addressee a " +
            "left join fetch a.userInformation " +
            "where r.id = :userId or a.id = :userId " +
            "and f.status = :status " +
            "and f.deletedAt is null")
    List<Friendship> findMyFriendships(@Param("userId") Long userId, @Param("status") FriendshipStatus status);

    // 친구 차단 확인
    @Query("select case when count(f) > 0 then true else false end " +
            "from Friendship f " +
            "where f.requester.id = :blockerId and f.addressee.id = :blockedId " +
            "and f.status = 'BLOCKED'")
    boolean isBlocked(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
}
