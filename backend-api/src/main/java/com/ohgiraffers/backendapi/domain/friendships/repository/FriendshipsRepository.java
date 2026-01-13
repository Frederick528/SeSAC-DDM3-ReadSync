package com.ohgiraffers.backendapi.domain.friendships.repository;

import com.ohgiraffers.backendapi.domain.friendships.entity.Friendships;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendshipsRepository extends JpaRepository<Friendships, Long> {

    // 1. 중복 요청 방지
    @Query("select case when count(f) > 0 then true else false end " +
            "from Friendships f " +
            "where f.requester = :userA and f.addressee = :userB " +
            "or (f.requester = :userB and f.addressee = :userA)")
    boolean existsByUsers(@Param("userA") User userA, @Param("userB") User userB);

    // 2. 특정 관계 조회(삭제나 상태 변경시)
    Optional<Friendships> findByRequesterAndAddressee(User requester, User addressee);

    
}
