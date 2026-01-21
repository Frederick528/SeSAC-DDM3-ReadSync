package com.ohgiraffers.backendapi.domain.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponseDTO {
    private Long friendshipId;
    private Long friendUserId;
    private String friendNickname;
    private String friendProfileImage;
    private String onlineStatus;      // 실시간 접속 상태, "OFFLINE"으로 하드코딩(추후 Redis 로직으로 교체)
}


