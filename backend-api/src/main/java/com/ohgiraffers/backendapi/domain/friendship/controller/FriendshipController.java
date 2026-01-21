package com.ohgiraffers.backendapi.domain.friendship.controller;

import com.ohgiraffers.backendapi.domain.friendship.dto.FriendListResponseDTO;
import com.ohgiraffers.backendapi.domain.friendship.service.FriendshipService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friendships", description = "친구 관계 관리 API")
@RestController
@RequestMapping("/v1/friendship")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    // TODO: Security 기능 완성시, getMyUserId 사용 메소드 '@AuthenticationPrincipal'로 받아오기
    @Operation(summary = "친구 요청 보내기")
    @PostMapping("/request/{addresseeId}")
    public ResponseEntity<Void> sendFriendRequest(
            @Parameter(hidden = true) @CurrentUserId Long requesterId, // 로그인한 유저 ID
            @PathVariable Long addresseeId // 요청 받을 상대방 ID
    ) {
        friendshipService.sendFriendRequest(requesterId, addresseeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 친구 목록 조회")
    @GetMapping
    public ResponseEntity<List<FriendListResponseDTO>> getMyFriends(
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        List<FriendListResponseDTO> response = friendshipService.getMyFriends(currentUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 요청 수락")
    @PostMapping("/{friendshipId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        friendshipService.acceptFriend(friendshipId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 거절")
    @PostMapping("/{friendshipId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        friendshipService.rejectFriend(friendshipId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 취소")
    @PostMapping("/{friendshipId}/cancel")
    public ResponseEntity<Void> cancelFriendRequest(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        friendshipService.cancelFriendRequest(friendshipId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 삭제")
    @PostMapping("/{friendshipId}")
    public ResponseEntity<Void> unfriend(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        friendshipService.unfriend(friendshipId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 차단")
    @PostMapping("/{friendshipId}/block")
    public ResponseEntity<Void> block(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        friendshipService.blockFriend(friendshipId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 차단 해제")
    @PostMapping("/{friendshipId}/unblock")
    public ResponseEntity<Void> unblock(
            @PathVariable Long friendshipId,
            @Parameter(hidden = true) @CurrentUserId Long currentUserId
    ) {
        friendshipService.unblockFriend(friendshipId, currentUserId);
        return ResponseEntity.ok().build();
    }

}
