package com.ohgiraffers.backendapi.domain.friendships.service;

import com.ohgiraffers.backendapi.domain.friendships.repository.FriendshipsRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipsService {

    private final FriendshipsRepository friendshipsRepository;
    private final UserRepository userRepository;

    // 친구 요청
    @Transactional
    public void sendFriendRequest(Long requesterId, Long addresseeId) {
        // 본인 요청 불가

    }
}
