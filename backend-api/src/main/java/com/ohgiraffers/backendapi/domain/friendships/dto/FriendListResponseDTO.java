package com.ohgiraffers.backendapi.domain.friendships.dto;

import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponseDTO {
    private Long friendshipId;
    private Long friendUserId;
    private String friendName;
    private String friendProfileImage;
    private String status;      // 실시간 접속 상태, "OFFLINE"으로 하드코딩(추후 Redis 로직으로 교체)
}
