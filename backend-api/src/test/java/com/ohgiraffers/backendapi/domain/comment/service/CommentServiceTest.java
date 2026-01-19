package com.ohgiraffers.backendapi.domain.comment.service;

import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.comment.dto.CommentRequestDTO;
import com.ohgiraffers.backendapi.domain.comment.dto.CommentResponseDTO;
import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import com.ohgiraffers.backendapi.domain.comment.repository.CommentRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChapterRepository chapterRepository;

    private User user;
    private UserInformation userInfo;
    private Chapter chapter;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // 1. User & UserInformation 가짜 객체 생성 및 연결
        user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", 1L); // ID 강제 주입

        userInfo = UserInformation.builder()
                .user(user)
                .nickname("테스트닉네임") // 닉네임 설정
                .build();

        // User와 UserInformation 양방향 연결 흉내 (Service에서 getter 체이닝을 위해)
        ReflectionTestUtils.setField(user, "userInformation", userInfo);

        // 2. Chapter 가짜 객체
        chapter = Chapter.builder().build();
        ReflectionTestUtils.setField(chapter, "chapterId", 100L);

        // 3. Comment 가짜 객체
        comment = Comment.builder()
                .user(user)
                .chapter(chapter)
                .content("기존 댓글 내용")
                .isSpoiler(false)
                .build();
        ReflectionTestUtils.setField(comment, "commentId", 10L);
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_Success() {
        // given
        CommentRequestDTO requestDTO = new CommentRequestDTO();
        ReflectionTestUtils.setField(requestDTO, "content", "새로운 댓글");
        ReflectionTestUtils.setField(requestDTO, "isSpoiler", false);
        ReflectionTestUtils.setField(requestDTO, "parentCommentId", null);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chapterRepository.findById(100L)).willReturn(Optional.of(chapter));
        given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        CommentResponseDTO result = commentService.createComment(1L, 100L, requestDTO);

        // then
        assertThat(result.getContent()).isEqualTo("새로운 댓글");
        assertThat(result.getNickname()).isEqualTo("테스트닉네임"); // UserInfo에서 가져오는지 확인
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 작성 성공 - 부모 댓글 연결 확인")
    void createReply_Success() {
        // given
        Comment parentComment = Comment.builder().user(user).chapter(chapter).content("부모").isSpoiler(false).build();
        ReflectionTestUtils.setField(parentComment, "commentId", 50L);

        CommentRequestDTO requestDTO = new CommentRequestDTO();
        ReflectionTestUtils.setField(requestDTO, "content", "대댓글 내용");
        ReflectionTestUtils.setField(requestDTO, "parentCommentId", 50L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chapterRepository.findById(100L)).willReturn(Optional.of(chapter));
        given(commentRepository.findById(50L)).willReturn(Optional.of(parentComment));

        // when
        CommentResponseDTO result = commentService.createComment(1L, 100L, requestDTO);

        // then
        assertThat(result.getParentCommentId()).isEqualTo(50L); // 부모 ID가 잘 들어갔는지
    }

    @Test
    @DisplayName("댓글 수정 성공 - 작성자 본인")
    void updateComment_Success() {
        // given
        CommentRequestDTO requestDTO = new CommentRequestDTO();
        ReflectionTestUtils.setField(requestDTO, "content", "수정된 내용");

        given(commentRepository.findById(10L)).willReturn(Optional.of(comment));

        // when
        CommentResponseDTO result = commentService.updateComment(1L, 10L, requestDTO);

        // then
        assertThat(result.getContent()).isEqualTo("수정된 내용");
        assertThat(result.isChanged()).isTrue(); // 수정 여부 true 확인
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자가 아님")
    void updateComment_Fail_Forbidden() {
        // given
        Long otherUserId = 999L;
        CommentRequestDTO requestDTO = new CommentRequestDTO();
        ReflectionTestUtils.setField(requestDTO, "content", "해킹 시도");

        given(commentRepository.findById(10L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(otherUserId, 10L, requestDTO))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("댓글 삭제 성공 - Soft Delete 확인")
    void deleteComment_Success() {
        // given
        given(commentRepository.findById(10L)).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(1L, 10L);

        // then
        assertThat(comment.getVisibilityStatus()).isEqualTo(VisibilityStatus.DELETED); // 상태가 DELETED로 변했는지
    }
}