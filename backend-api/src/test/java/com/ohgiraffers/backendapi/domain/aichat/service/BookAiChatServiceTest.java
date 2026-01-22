package com.ohgiraffers.backendapi.domain.aichat.service;

import com.ohgiraffers.backendapi.domain.aichat.dto.*;
import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChat;
import com.ohgiraffers.backendapi.domain.aichat.entity.BookAiChatRoom;
import com.ohgiraffers.backendapi.domain.aichat.enums.ChatType;
import com.ohgiraffers.backendapi.domain.aichat.repository.BookAiChatRepository;
import com.ohgiraffers.backendapi.domain.aichat.repository.BookAiChatRoomRepository;
import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AI 채팅 서비스 테스트
 * 채팅방 관리 및 메시지 처리 기능을 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
class BookAiChatServiceTest {

    @InjectMocks
    private BookAiChatService bookAiChatService;

    @Mock
    private BookAiChatRoomRepository chatRoomRepository;

    @Mock
    private BookAiChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private WebClient aiWebClient;

    private User user;
    private User otherUser;
    private Book book;
    private Chapter chapter;
    private BookAiChatRoom chatRoom;
    private BookAiChat chat;

    @BeforeEach
    void setUp() {
        // 테스트용 User 생성
        user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", 1L);

        otherUser = User.builder().build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);

        // 테스트용 Book 생성
        book = Book.builder()
                .title("테스트 도서")
                .build();
        ReflectionTestUtils.setField(book, "bookId", 1L);

        // 테스트용 Chapter 생성
        chapter = Chapter.builder()
                .book(book)
                .chapterName("1장 시작")
                .sequence(1)
                .bookContentPath("/path/to/chapter1.txt")
                .build();
        ReflectionTestUtils.setField(chapter, "chapterId", 1L);

        // 테스트용 ChatRoom 생성
        chatRoom = BookAiChatRoom.builder()
                .user(user)
                .chapter(chapter)
                .title("1장 시작 대화")
                .build();
        ReflectionTestUtils.setField(chatRoom, "aiRoomId", 1L);

        // 테스트용 Chat 생성
        chat = BookAiChat.builder()
                .chatRoom(chatRoom)
                .user(user)
                .chatType(ChatType.CONTENT_QA)
                .userMessage("주인공이 왜 화났어?")
                .aiMessage("주인공은 배신감을 느껴서 화가 났습니다.")
                .tokenCount(150)
                .responseTimeMs(500)
                .build();
        ReflectionTestUtils.setField(chat, "chatId", 1L);
    }

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChatRoom {

        @Test
        @DisplayName("성공: 새 채팅방을 생성한다")
        void success_createNewRoom() {
            // given
            BookAiChatRoomRequestDTO request = new BookAiChatRoomRequestDTO();
            ReflectionTestUtils.setField(request, "chapterId", 1L);
            ReflectionTestUtils.setField(request, "title", "테스트 채팅방");

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(chapterRepository.findById(1L)).willReturn(Optional.of(chapter));
            given(chatRoomRepository.findByUserAndChapter(user, chapter)).willReturn(Optional.empty());
            given(chatRoomRepository.save(any(BookAiChatRoom.class))).willReturn(chatRoom);

            // when
            BookAiChatRoomResponseDTO result = bookAiChatService.createChatRoom(1L, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAiRoomId()).isEqualTo(1L);
            verify(chatRoomRepository, times(1)).save(any(BookAiChatRoom.class));
        }

        @Test
        @DisplayName("성공: 기존 채팅방이 있으면 기존 채팅방을 반환한다")
        void success_returnExistingRoom() {
            // given
            BookAiChatRoomRequestDTO request = new BookAiChatRoomRequestDTO();
            ReflectionTestUtils.setField(request, "chapterId", 1L);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(chapterRepository.findById(1L)).willReturn(Optional.of(chapter));
            given(chatRoomRepository.findByUserAndChapter(user, chapter)).willReturn(Optional.of(chatRoom));

            // when
            BookAiChatRoomResponseDTO result = bookAiChatService.createChatRoom(1L, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAiRoomId()).isEqualTo(1L);
            verify(chatRoomRepository, never()).save(any(BookAiChatRoom.class));
        }

        @Test
        @DisplayName("실패: 사용자를 찾을 수 없다")
        void fail_userNotFound() {
            // given
            BookAiChatRoomRequestDTO request = new BookAiChatRoomRequestDTO();
            ReflectionTestUtils.setField(request, "chapterId", 1L);

            given(userRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.createChatRoom(1L, request));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 챕터를 찾을 수 없다")
        void fail_chapterNotFound() {
            // given
            BookAiChatRoomRequestDTO request = new BookAiChatRoomRequestDTO();
            ReflectionTestUtils.setField(request, "chapterId", 999L);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(chapterRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.createChatRoom(1L, request));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHAPTER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("채팅방 조회")
    class GetChatRoom {

        @Test
        @DisplayName("성공: 채팅방을 조회한다")
        void success() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
            given(chatRepository.countByChatRoom(chatRoom)).willReturn(5L);

            // when
            BookAiChatRoomResponseDTO result = bookAiChatService.getChatRoom(1L, 1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAiRoomId()).isEqualTo(1L);
            assertThat(result.getMessageCount()).isEqualTo(5L);
        }

        @Test
        @DisplayName("실패: 채팅방을 찾을 수 없다")
        void fail_roomNotFound() {
            // given
            given(chatRoomRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.getChatRoom(1L, 999L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_ROOM_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 채팅방 소유자가 아니다")
        void fail_notOwner() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.getChatRoom(2L, 1L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_NOT_OWNER);
        }
    }

    @Nested
    @DisplayName("채팅방 목록 조회")
    class GetChatRoomsByUser {

        @Test
        @DisplayName("성공: 사용자의 채팅방 목록을 조회한다")
        void success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<BookAiChatRoom> page = new PageImpl<>(List.of(chatRoom), pageable, 1);

            given(chatRoomRepository.findByUserId(1L, pageable)).willReturn(page);

            // when
            Page<BookAiChatRoomResponseDTO> result = bookAiChatService.getChatRoomsByUser(1L, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getAiRoomId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("채팅방 삭제")
    class DeleteChatRoom {

        @Test
        @DisplayName("성공: 채팅방을 삭제한다 (Soft Delete)")
        void success() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

            // when
            bookAiChatService.deleteChatRoom(1L, 1L);

            // then
            // Soft delete이므로 deletedAt이 설정되어야 함
            assertThat(chatRoom.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("실패: 채팅방 소유자가 아니면 삭제할 수 없다")
        void fail_notOwner() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.deleteChatRoom(2L, 1L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_NOT_OWNER);
        }
    }

    @Nested
    @DisplayName("채팅방 제목 수정")
    class UpdateChatRoomTitle {

        @Test
        @DisplayName("성공: 채팅방 제목을 수정한다")
        void success() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

            // when
            BookAiChatRoomResponseDTO result = bookAiChatService.updateChatRoomTitle(1L, 1L, "새로운 제목");

            // then
            assertThat(chatRoom.getTitle()).isEqualTo("새로운 제목");
        }

        @Test
        @DisplayName("실패: 채팅방 소유자가 아니면 수정할 수 없다")
        void fail_notOwner() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.updateChatRoomTitle(2L, 1L, "새로운 제목"));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_NOT_OWNER);
        }
    }

    @Nested
    @DisplayName("채팅 기록 조회")
    class GetChatHistory {

        @Test
        @DisplayName("성공: 채팅 기록을 조회한다")
        void success() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
            given(chatRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom)).willReturn(List.of(chat));

            // when
            List<BookAiChatResponseDTO> result = bookAiChatService.getChatHistory(1L, 1L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getChatId()).isEqualTo(1L);
            assertThat(result.get(0).getUserMessage()).isEqualTo("주인공이 왜 화났어?");
            assertThat(result.get(0).getAiMessage()).isEqualTo("주인공은 배신감을 느껴서 화가 났습니다.");
        }

        @Test
        @DisplayName("실패: 채팅방 소유자가 아니면 조회할 수 없다")
        void fail_notOwner() {
            // given
            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.getChatHistory(2L, 1L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_NOT_OWNER);
        }
    }

    @Nested
    @DisplayName("채팅 기록 페이징 조회")
    class GetChatHistoryPaged {

        @Test
        @DisplayName("성공: 채팅 기록을 페이징으로 조회한다")
        void success() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<BookAiChat> page = new PageImpl<>(List.of(chat), pageable, 1);

            given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
            given(chatRepository.findByChatRoom(chatRoom, pageable)).willReturn(page);

            // when
            Page<BookAiChatResponseDTO> result = bookAiChatService.getChatHistoryPaged(1L, 1L, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getChatId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("AI 답변 평가")
    class RateAiResponse {

        @Test
        @DisplayName("성공: AI 답변을 평가한다")
        void success() {
            // given
            given(chatRepository.findById(1L)).willReturn(Optional.of(chat));

            // when
            bookAiChatService.rateAiResponse(1L, 1L, 5);

            // then
            assertThat(chat.getRating()).isEqualTo(5);
        }

        @Test
        @DisplayName("실패: 유효하지 않은 평점 (0점)")
        void fail_invalidRating_zero() {
            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.rateAiResponse(1L, 1L, 0));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_RATING_INVALID);
        }

        @Test
        @DisplayName("실패: 유효하지 않은 평점 (6점)")
        void fail_invalidRating_six() {
            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.rateAiResponse(1L, 1L, 6));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_RATING_INVALID);
        }

        @Test
        @DisplayName("실패: 채팅 메시지를 찾을 수 없다")
        void fail_chatNotFound() {
            // given
            given(chatRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.rateAiResponse(1L, 999L, 5));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 채팅 메시지의 소유자가 아니다")
        void fail_notOwner() {
            // given
            given(chatRepository.findById(1L)).willReturn(Optional.of(chat));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> bookAiChatService.rateAiResponse(2L, 1L, 5));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_CHAT_NOT_OWNER);
        }
    }
}
