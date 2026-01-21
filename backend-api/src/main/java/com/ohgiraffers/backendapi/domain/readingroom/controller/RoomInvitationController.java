package com.ohgiraffers.backendapi.domain.readingroom.controller;

import com.ohgiraffers.backendapi.domain.readingroom.service.RoomInvitationService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Room Invitation", description = "독서룸 초대장 관련 API")
@RestController
@RequestMapping("/v1/room-invitations")
@RequiredArgsConstructor
public class RoomInvitationController {

    private final RoomInvitationService invitationService;

    @Operation(summary = "초대장 발송 (방장 전용)")
    @PostMapping("/invite")
    public ResponseEntity<Void> inviteUser(
            @Parameter(hidden = true) @CurrentUserId Long hostId,
            @RequestParam Long roomId,
            @RequestParam Long targetUserId) {

        invitationService.inviteUser(roomId, hostId, targetUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대장 수락")
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @PathVariable Long invitationId) {

        invitationService.acceptInvitation(invitationId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대장 거절")
    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @PathVariable Long invitationId) {

        invitationService.rejectInvitation(invitationId, userId);
        return ResponseEntity.ok().build();
    }
}
