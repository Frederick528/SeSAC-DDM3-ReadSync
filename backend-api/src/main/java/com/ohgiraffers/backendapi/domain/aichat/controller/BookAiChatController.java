package com.ohgiraffers.backendapi.domain.aichat.controller;

import com.ohgiraffers.backendapi.domain.aichat.dto.*;
import com.ohgiraffers.backendapi.domain.aichat.service.BookAiChatService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 채팅 컨트롤러
 * 도서 뷰어에서 AI와 대화하기 위한 API를 제공합니다.
 */
@Tag(name = "AI Chat (도서 AI 채팅)", description = "도서 내용 기반 AI 채팅 API")
@RestController
@RequestMapping("/v1/ai-chat")
@RequiredArgsConstructor
public class BookAiChatController {

    private final BookAiChatService bookAiChatService;

    /* ========== 채팅방 관리 ========== */

    @Operation(summary = "[사용자/관리자] 채팅방 생성", description = "특정 챕터에 대한 AI 채팅방을 생성합니다. 동일 챕터의 채팅방이 이미 있으면 기존 채팅방을 반환합니다.")
    @PostMapping("/rooms")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookAiChatRoomResponseDTO> createChatRoom(
            @CurrentUserId Long userId,
            @RequestBody BookAiChatRoomRequestDTO request) {
        BookAiChatRoomResponseDTO response = bookAiChatService.createChatRoom(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 채팅방 목록 조회", description = "사용자의 모든 AI 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<BookAiChatRoomResponseDTO>> getChatRooms(
            @CurrentUserId Long userId,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookAiChatRoomResponseDTO> response = bookAiChatService.getChatRoomsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 채팅방 상세 조회", description = "특정 AI 채팅방의 상세 정보를 조회합니다.")
    @GetMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookAiChatRoomResponseDTO> getChatRoom(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId) {
        BookAiChatRoomResponseDTO response = bookAiChatService.getChatRoom(userId, roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 채팅방 제목 수정", description = "AI 채팅방의 제목을 수정합니다.")
    @PutMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookAiChatRoomResponseDTO> updateChatRoomTitle(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @Parameter(description = "새 제목") @RequestParam String title) {
        BookAiChatRoomResponseDTO response = bookAiChatService.updateChatRoomTitle(userId, roomId, title);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 채팅방 삭제", description = "AI 채팅방을 삭제합니다. (Soft Delete)")
    @DeleteMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deleteChatRoom(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId) {
        bookAiChatService.deleteChatRoom(userId, roomId);
        return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    }

    /* ========== 채팅 메시지 ========== */

    @Operation(summary = "[사용자/관리자] 채팅 기록 조회", description = "특정 채팅방의 모든 대화 기록을 조회합니다.")
    @GetMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BookAiChatResponseDTO>> getChatHistory(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId) {
        List<BookAiChatResponseDTO> response = bookAiChatService.getChatHistory(userId, roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 채팅 기록 페이징 조회", description = "특정 채팅방의 대화 기록을 페이징하여 조회합니다.")
    @GetMapping("/rooms/{roomId}/messages/paged")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<BookAiChatResponseDTO>> getChatHistoryPaged(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<BookAiChatResponseDTO> response = bookAiChatService.getChatHistoryPaged(userId, roomId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 메시지 전송 (일반)", description = "AI에게 질문을 보내고 응답을 받습니다.")
    @PostMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookAiChatResponseDTO> sendMessage(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @RequestBody BookAiChatRequestDTO request) {
        BookAiChatResponseDTO response = bookAiChatService.sendMessage(userId, roomId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 메시지 전송 (스트리밍)", description = "AI에게 질문을 보내고 SSE로 실시간 응답을 받습니다.")
    @PostMapping(value = "/rooms/{roomId}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Flux<String> sendMessageStream(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @RequestBody BookAiChatRequestDTO request) {
        return bookAiChatService.sendMessageStream(userId, roomId, request);
    }

    @Operation(summary = "[사용자/관리자] AI 답변 평가", description = "AI 답변의 품질을 1~5점으로 평가합니다.")
    @PutMapping("/messages/{chatId}/rating")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> rateAiResponse(
            @CurrentUserId Long userId,
            @Parameter(description = "채팅 메시지 ID") @PathVariable Long chatId,
            @Parameter(description = "평점 (1~5)") @RequestParam Integer rating) {
        bookAiChatService.rateAiResponse(userId, chatId, rating);
        return ResponseEntity.ok("평가가 저장되었습니다.");
    }
}
