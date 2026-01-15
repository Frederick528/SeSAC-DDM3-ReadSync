package com.ohgiraffers.backendapi.domain.friendships.service;

import com.ohgiraffers.backendapi.domain.friendships.dto.FriendListResponseDTO;
import com.ohgiraffers.backendapi.domain.friendships.entity.Friendships;
import com.ohgiraffers.backendapi.domain.friendships.enums.FriendshipsStatus;
import com.ohgiraffers.backendapi.domain.friendships.repository.FriendshipsRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        if (requesterId.equals(addresseeId)) {
            throw new CustomException(ErrorCode.CANNOT_REQUEST_TO_SELF);
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESSEE_NOT_FOUND));

        // 중복 요청 방지
        if (friendshipsRepository.existsByUsers(requester, addressee)) {
            throw new CustomException(ErrorCode.ALREADY_FRIENDS);
        }

        // 친구 저장
        Friendships friendships = Friendships.builder()
                .requester(requester)
                .addressee(addressee)
                .status(FriendshipsStatus.ACCEPTED)
                .build();

        friendshipsRepository.save(friendships);

    }

    // 친구 목록 조회
    public List<FriendListResponseDTO> getMyFriends(Long myUserId) {
        List<Friendships> friendships = friendshipsRepository.findMyFriendships(myUserId, FriendshipsStatus.ACCEPTED);

        return friendships.stream()
                .map(f -> {
                    User friend = f.getRequester().getId().equals(myUserId) ? f.getAddressee() : f.getRequester();

                    return new FriendListResponseDTO(
                            f.getFriendshipId(),
                            friend.getId(),
                            friend.getName(),
                            friend.getUserInformation().getProfileImg(),
                            "OFFLINE"   // 실시간 접속 상태
                    );
                })
                .collect(Collectors.toList());
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriend(Long friendshipsId, Long myUserId) {
        Friendships friendships = getFriendshipOrThrow(friendshipsId);

        validateFriendshipOwner(friendships, myUserId);

        friendships.accept();
    }

    // 친구 요청 거절
    @Transactional
    public void rejectFriend(Long friendshipsId, Long myUserId) {
        Friendships friendships = getFriendshipOrThrow(friendshipsId);

        validateFriendshipOwner(friendships, myUserId);

        friendships.reject();
    }

    // 친구 삭제
    @Transactional
    public void unfriend(Long friendshipsId, Long myUserId) {
        Friendships friendships = getFriendshipOrThrow(friendshipsId);

        validateFriendshipOwner(friendships, myUserId);

        friendships.unfriend();
    }

    // 상대방 차단
    @Transactional
    public void blockFriend(Long friendshipsId, Long myUserId) {
        Friendships friendships = getFriendshipOrThrow(friendshipsId);

        validateFriendshipOwner(friendships, myUserId);

        friendships.block();
    }

    // 상대방 차단 해제
    @Transactional
    public void unblockFriend(Long friendshipsId, Long myUserId) {
        Friendships friendships = getFriendshipOrThrow(friendshipsId);

        validateFriendshipOwner(friendships, myUserId);

        friendships.unblockFriendships();
    }



    // 내부 편의 메서드
    // id 조회 에러
    private Friendships getFriendshipOrThrow(Long friendshipId) {
        return friendshipsRepository.findById(friendshipId)
                .orElseThrow(() -> new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
    }

    // 당사자 확인 (내가 이 관계의 주인공(A or B)이 맞는지?)
    private void validateFriendshipOwner(Friendships friendship, Long myUserId) {
        boolean isRequester = friendship.getRequester().getId().equals(myUserId);
        boolean isAddressee = friendship.getAddressee().getId().equals(myUserId);

        if (!isRequester && !isAddressee) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }
    }
}
