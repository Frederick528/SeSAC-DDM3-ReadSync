package com.ohgiraffers.backendapi.domain.friendship.service;

import com.ohgiraffers.backendapi.domain.friendship.dto.FriendListResponseDTO;
import com.ohgiraffers.backendapi.domain.friendship.entity.Friendship;
import com.ohgiraffers.backendapi.domain.friendship.enums.FriendshipStatus;
import com.ohgiraffers.backendapi.domain.friendship.repository.FriendshipRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
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
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
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
        if (friendshipRepository.existsByUsers(requester, addressee)) {
            throw new CustomException(ErrorCode.ALREADY_FRIENDS);
        }

        // 친구 저장
        Friendship friendship = Friendship.builder()
                .requester(requester)
                .addressee(addressee)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);

    }

    // 친구 목록 조회
    public List<FriendListResponseDTO> getMyFriends(Long myUserId) {
        List<Friendship> friendships = friendshipRepository.findMyFriendships(myUserId, FriendshipStatus.ACCEPTED);

        return friendships.stream()
                .map(f -> {
                    User friend = f.getRequester().getId().equals(myUserId) ? f.getAddressee() : f.getRequester();

                    UserInformation info = friend.getUserInformation();

                    // 1. 표시할 이름 결정 로직 (닉네임 -> 아이디 -> 기본값)
                    String displayName;

                    if (info != null && info.getNickname() != null) {
                        displayName = info.getNickname();
                    } else if (friend.getLoginId() != null) {
                        displayName = friend.getLoginId();
                    } else {
                        // 소셜 로그인 유저라 loginId도 없는 경우를 대비
                        displayName = "알 수 없음";
                    }

                    String profileImg = (info != null) ? info.getProfileImage() : null;

                    return new FriendListResponseDTO(
                            f.getFriendshipId(),
                            friend.getId(),
                            displayName,
                            profileImg,
                            "OFFLINE"   // 실시간 접속 상태
                    );
                })
                .collect(Collectors.toList());
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriend(Long friendshipId, Long myUserId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        // 수신자(addressee) 본인만 수락 가능
        if (!friendship.getAddressee().getId().equals(myUserId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }

        friendship.accept();
    }

    // 친구 요청 거절
    @Transactional
    public void rejectFriend(Long friendshipId, Long myUserId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        // 수신자(addressee) 본인만 거절 가능
        if (!friendship.getAddressee().getId().equals(myUserId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }

        friendship.reject();
    }

    // 친구 요청 취소
    @Transactional
    public void cancelFriendRequest(Long friendshipId, Long myUserId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        // 요청자(requester) 본인만 취소 가능
        if (!friendship.getRequester().getId().equals(myUserId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }

        // 아직 PENDING 상태일 때만 취소 가능
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_STATUS);
        }

        friendship.cancel();
    }


    // 친구 삭제
    @Transactional
    public void unfriend(Long friendshipId, Long myUserId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        validateFriendshipOwner(friendship, myUserId);

        friendship.unfriend();
    }

    // 상대방 차단
    @Transactional
    public void blockFriend(Long friendshipId, Long myUserId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        validateFriendshipOwner(friendship, myUserId);

        friendship.block();
    }

    // 상대방 차단 해제
    @Transactional
    public void unblockFriend(Long friendshipId, Long myUserId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        validateFriendshipOwner(friendship, myUserId);

        friendship.unblockFriendships();
    }



    // 내부 편의 메서드
    // id 조회 에러
    private Friendship getFriendshipOrThrow(Long friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
    }

    // 당사자 확인 (내가 이 관계의 주인공(A or B)이 맞는지?)
    private void validateFriendshipOwner(Friendship friendship, Long myUserId) {
        boolean isRequester = friendship.getRequester().getId().equals(myUserId);
        boolean isAddressee = friendship.getAddressee().getId().equals(myUserId);

        if (!isRequester && !isAddressee) {
            throw new CustomException(ErrorCode.NO_AUTHORITY_TO_UPDATE);
        }
    }
}
