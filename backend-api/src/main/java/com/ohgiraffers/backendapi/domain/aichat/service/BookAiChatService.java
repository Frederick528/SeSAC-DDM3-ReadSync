package com.ohgiraffers.backendapi.domain.aichat.service;

import com.ohgiraffers.backendapi.domain.aichat.dto.*;
import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChat;
import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChatRoom;
import com.ohgiraffers.backendapi.domain.aichat.enums.ChatType;
import com.ohgiraffers.backendapi.domain.aichat.repository.BookAiChatRepository;
import com.ohgiraffers.backendapi.domain.aichat.repository.BookAiChatRoomRepository;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 채팅 서비스
 * 채팅방 관리 및 Python AI 서버와의 통신을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookAiChatService {

    private final BookAiChatRoomRepository chatRoomRepository;
    private final BookAiChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final WebClient aiWebClient;

    @Value("${ai.server.timeout:30}")
    private int aiServerTimeout;

    /* ========== 채팅방 관리 ========== */

    /**
     * [1] 채팅방 생성
     * 동일한 사용자+챕터 조합의 채팅방이 있으면 기존 채팅방 반환
     */
    @Transactional
    public BookAiChatRoomResponseDTO createChatRoom(Long userId, BookAiChatRoomRequestDTO request) {
        User user = findUserById(userId);
        Chapter chapter = findChapterById(request.getChapterId());

        // 기존 채팅방 확인
        return chatRoomRepository.findByUserAndChapter(user, chapter)
                .map(BookAiChatRoomResponseDTO::from)
                .orElseGet(() -> {
                    // 새 채팅방 생성
                    String title = (request.getTitle() != null && !request.getTitle().isEmpty())
                            ? request.getTitle()
                            : BookAiChatRoom.generateDefaultTitle(chapter);

                    BookAiChatRoom room = BookAiChatRoom.builder()
                            .user(user)
                            .chapter(chapter)
                            .title(title)
                            .build();

                    return BookAiChatRoomResponseDTO.from(chatRoomRepository.save(room));
                });
    }

    /**
     * [2] 채팅방 단건 조회
     */
    public BookAiChatRoomResponseDTO getChatRoom(Long userId, Long roomId) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);

        long messageCount = chatRepository.countByChatRoom(room);
        return BookAiChatRoomResponseDTO.from(room, messageCount);
    }

    /**
     * [3] 사용자의 채팅방 목록 조회
     */
    public Page<BookAiChatRoomResponseDTO> getChatRoomsByUser(Long userId, Pageable pageable) {
        return chatRoomRepository.findByUserId(userId, pageable)
                .map(BookAiChatRoomResponseDTO::from);
    }

    /**
     * [4] 채팅방 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteChatRoom(Long userId, Long roomId) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);
        room.delete();
    }

    /**
     * [5] 채팅방 제목 수정
     */
    @Transactional
    public BookAiChatRoomResponseDTO updateChatRoomTitle(Long userId, Long roomId, String newTitle) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);
        room.updateTitle(newTitle);
        return BookAiChatRoomResponseDTO.from(room);
    }

    /* ========== 채팅 메시지 관리 ========== */

    /**
     * [6] 채팅 기록 조회
     */
    public List<BookAiChatResponseDTO> getChatHistory(Long userId, Long roomId) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);

        return chatRepository.findByChatRoomOrderByCreatedAtAsc(room).stream()
                .map(BookAiChatResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * [7] 채팅 기록 페이징 조회
     */
    public Page<BookAiChatResponseDTO> getChatHistoryPaged(Long userId, Long roomId, Pageable pageable) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);

        return chatRepository.findByChatRoom(room, pageable)
                .map(BookAiChatResponseDTO::from);
    }

    /**
     * [8] 메시지 전송 (일반 HTTP 요청)
     * Python AI 서버에 요청을 보내고 응답을 받아 DB에 저장합니다.
     */
    @Transactional
    public BookAiChatResponseDTO sendMessage(Long userId, Long roomId, BookAiChatRequestDTO request) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);
        User user = findUserById(userId);

        long startTime = System.currentTimeMillis();

        try {
            // Python AI 서버 호출
            Map<String, Object> aiResponse = callAiServer(
                    room.getChapter().getChapterId(),
                    request.getUserMessage(),
                    request.getChatType());

            long responseTimeMs = System.currentTimeMillis() - startTime;

            // AI 응답에서 데이터 추출
            String aiMessage = (String) aiResponse.getOrDefault("answer", "죄송합니다. 응답을 생성할 수 없습니다.");
            Integer tokenCount = (Integer) aiResponse.get("token_count");

            // DB에 저장
            BookAiChat chat = BookAiChat.builder()
                    .chatRoom(room)
                    .user(user)
                    .chatType(request.getChatType() != null ? request.getChatType() : ChatType.CONTENT_QA)
                    .userMessage(request.getUserMessage())
                    .aiMessage(aiMessage)
                    .tokenCount(tokenCount)
                    .responseTimeMs((int) responseTimeMs)
                    .build();

            return BookAiChatResponseDTO.from(chatRepository.save(chat));

        } catch (WebClientResponseException e) {
            log.error("AI 서버 호출 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.AI_SERVER_ERROR);
        }
    }

    /**
     * [9] 메시지 전송 (SSE 스트리밍)
     * Python AI 서버에서 스트리밍 응답을 받아 클라이언트에 전달합니다.
     * 스트리밍 완료 후 DB에 저장합니다.
     */
    @Transactional
    public Flux<String> sendMessageStream(Long userId, Long roomId, BookAiChatRequestDTO request) {
        BookAiChatRoom room = findChatRoomById(roomId);
        validateRoomOwner(room, userId);
        User user = findUserById(userId);

        long startTime = System.currentTimeMillis();
        StringBuilder fullResponse = new StringBuilder();

        return callAiServerStream(
                room.getChapter().getChapterId(),
                request.getUserMessage(),
                request.getChatType())
                .doOnNext(chunk -> fullResponse.append(chunk))
                .doOnComplete(() -> {
                    long responseTimeMs = System.currentTimeMillis() - startTime;

                    // 스트리밍 완료 후 DB에 저장
                    BookAiChat chat = BookAiChat.builder()
                            .chatRoom(room)
                            .user(user)
                            .chatType(request.getChatType() != null ? request.getChatType() : ChatType.CONTENT_QA)
                            .userMessage(request.getUserMessage())
                            .aiMessage(fullResponse.toString())
                            .responseTimeMs((int) responseTimeMs)
                            .build();

                    chatRepository.save(chat);
                    log.info("AI 채팅 저장 완료 - roomId: {}, 응답시간: {}ms", roomId, responseTimeMs);
                })
                .doOnError(e -> {
                    log.error("AI 스트리밍 실패: {}", e.getMessage());
                });
    }

    /**
     * [10] AI 답변 평가
     */
    @Transactional
    public void rateAiResponse(Long userId, Long chatId, Integer rating) {
        // 평점 유효성 검증
        if (rating == null || rating < 1 || rating > 5) {
            throw new CustomException(ErrorCode.AI_RATING_INVALID);
        }

        BookAiChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_CHAT_NOT_FOUND));

        // 소유자 검증
        if (!chat.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.AI_CHAT_NOT_OWNER);
        }

        chat.updateRating(rating);
    }

    /* ========== Python AI 서버 통신 ========== */

    /**
     * Python AI 서버 호출 (일반 HTTP)
     */
    private Map<String, Object> callAiServer(Long chapterId, String userMessage, ChatType chatType) {
        return aiWebClient.post()
                .uri("/generate-answer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "chapter_id", chapterId,
                        "user_message", userMessage,
                        "chat_type", chatType != null ? chatType.name() : ChatType.CONTENT_QA.name()))
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(aiServerTimeout))
                .block();
    }

    /**
     * Python AI 서버 호출 (SSE 스트리밍)
     */
    private Flux<String> callAiServerStream(Long chapterId, String userMessage, ChatType chatType) {
        return aiWebClient.post()
                .uri("/generate-answer/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "chapter_id", chapterId,
                        "user_message", userMessage,
                        "chat_type", chatType != null ? chatType.name() : ChatType.CONTENT_QA.name()))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(aiServerTimeout));
    }

    /* ========== Helper Methods ========== */

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Chapter findChapterById(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));
    }

    private BookAiChatRoom findChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_CHAT_ROOM_NOT_FOUND));
    }

    private void validateRoomOwner(BookAiChatRoom room, Long userId) {
        if (!room.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.AI_CHAT_NOT_OWNER);
        }
    }
}
