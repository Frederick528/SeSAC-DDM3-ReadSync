package com.ohgiraffers.backendapi.domain.friendships.controller;

import com.ohgiraffers.backendapi.domain.friendships.dto.FriendListResponseDTO;
import com.ohgiraffers.backendapi.domain.friendships.service.FriendshipsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friendships", description = "친구 관계 관리 API")
@RestController
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
public class FriendshipsController {

    private FriendshipsService friendshipsService;

    // TODO: Security 기능 완성시, getMyUserId 사용 메소드 '@AuthenticationPrincipal'로 받아오기
    @Operation(summary = "친구 요청 보내기")
    @PostMapping("/request/{targetId}")
    public ResponseEntity<Void> sendFriendRequest(@PathVariable Long addresseeId) {
        friendshipsService.sendFriendRequest(getMyUserId(), addresseeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 친구 목록 조회")
    @GetMapping
    public ResponseEntity<List<FriendListResponseDTO>> getMyFriends() {
        List<FriendListResponseDTO> response = friendshipsService.getMyFriends(getMyUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 요청 수락")
    @PostMapping("/{friendshipsId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long friendshipsId) {
        friendshipsService.acceptFriend(friendshipsId, getMyUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 거절")
    @PostMapping("/{friendshipsId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long friendshipsId) {
        friendshipsService.rejectFriend(friendshipsId, getMyUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 취소")
    @PostMapping("/{friendshipsId}/cancel")
    public ResponseEntity<Void> cancelFriendRequest(@PathVariable Long friendshipsId) {
        friendshipsService.cancelFriendRequest(friendshipsId, getMyUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 삭제")
    @PostMapping("/{friendshipsId}")
    public ResponseEntity<Void> unfriend(@PathVariable Long friendshipsId) {
        friendshipsService.unfriend(friendshipsId, getMyUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 차단")
    @PostMapping("/{friendshipsId}/block")
    public ResponseEntity<Void> block(@PathVariable Long friendshipsId) {
        friendshipsService.blockFriend(friendshipsId, getMyUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 차단 해제")
    @PostMapping("/{friendshipsId}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable Long friendshipsId) {
        friendshipsService.unblockFriend(friendshipsId, getMyUserId());
        return ResponseEntity.ok().build();
    }

    // 테스트용 Helper Method()
    // TODO: Security 기능 완성시, 삭제하고 주석 해제
    private Long getMyUserId() {
        return 1L;
    }

    // 실제 Helper Method
    // TODO: 실제 UserDetails 파일 이름 확인하기
//    private Long getUserId(UserDetails principal) {
//        if (principal instanceof CustomUserDetails) {
//            // User 팀이 만든 메서드 이름이 getUserId() 인지 getId() 인지 확인 필요!
//            return ((CustomUserDetails) principal).getUserId();
//        }
//        // 예외 처리: 로그인하지 않은 사용자가 접근했거나, 토큰 설정이 잘못된 경우
//        throw new IllegalStateException("인증된 사용자 정보가 올바르지 않습니다.");
//    }
}
