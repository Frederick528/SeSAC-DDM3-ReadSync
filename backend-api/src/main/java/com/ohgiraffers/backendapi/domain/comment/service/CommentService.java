package com.ohgiraffers.backendapi.domain.comment.service;

import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.chapter.repository.ChapterRepository;
import com.ohgiraffers.backendapi.domain.comment.dto.CommentRequestDTO;
import com.ohgiraffers.backendapi.domain.comment.dto.CommentResponseDTO;
import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import com.ohgiraffers.backendapi.domain.comment.repository.CommentRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;

    /* [1] 댓글 작성 (일반 + 대댓글) */
    @Transactional
    public CommentResponseDTO createComment(Long userId, Long chapterId, CommentRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAPTER_NOT_FOUND));

        // 부모 댓글 처리
        Comment parentComment = null;
        if (requestDTO.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            // 삭제된 댓글에는 대댓글 못 달게
            if (parentComment.getVisibilityStatus() == VisibilityStatus.DELETED) {
                throw new CustomException(ErrorCode.CANNOT_REPLY_TO_DELETED);
            }

            // 신고 비노출 댓글에는 대댓글 못 달게
            if (parentComment.getVisibilityStatus() == VisibilityStatus.SUSPENDED) {
                throw new CustomException(ErrorCode.CANNOT_REPLY_TO_SUSPENDED);
            }
        }

        Comment comment = Comment.builder()
                .user(user)
                .chapter(chapter)
                .content(requestDTO.getContent())
                .isSpoiler(requestDTO.isSpoiler())
                .parentComment(parentComment) // null이나 객체 중 하나
                .build();

        return toReponseDTO(commentRepository.save(comment));
    }

    /* [2] 댓글 목록 조회 */
    public List<CommentResponseDTO> getCommentsByChapter(Long chapterId) {
        // 해당 챕터의 댓글 전체 조회
        List<Comment> comments = commentRepository.findByChapter_ChapterIdOrderByCreatedAtAsc(chapterId);

        // Entity List -> DTO List 변환
        return comments.stream()
                .map(this::toReponseDTO)
                .collect(Collectors.toList());
    }

    /* [2-1] 댓글 단건 조회 */
    public CommentResponseDTO getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 삭제되거나 정지된 댓글은 조회 불가
        if (comment.getVisibilityStatus() == VisibilityStatus.DELETED) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (comment.getVisibilityStatus() == VisibilityStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.COMMENT_SUSPENDED);
        }

        return toReponseDTO(comment);
    }

    /* [2-2] 본인 댓글 목록 조회 */
    public List<CommentResponseDTO> getMyComments(Long userId) {
        List<Comment> comments = commentRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        return comments.stream()
                .filter(c -> c.getVisibilityStatus() != VisibilityStatus.DELETED)
                .map(this::toReponseDTO)
                .collect(Collectors.toList());
    }

    /* [3] 댓글 수정 */
    @Transactional
    public CommentResponseDTO updateComment(Long userId, Long commentId, CommentRequestDTO commentRequestDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 작성자 검증
        validateWriter(comment, userId);

        // 삭제된 댓글은 수정 불가 처리
        if (comment.getVisibilityStatus() == VisibilityStatus.DELETED) {
            throw new CustomException(ErrorCode.CANNOT_REPLY_TO_DELETED);
        }

        // 신고로 비노출된 댓글은 수정 불가 처리
        if (comment.getVisibilityStatus() == VisibilityStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.CANNOT_REPLY_TO_SUSPENDED);
        }

        comment.updateContent(commentRequestDTO.getContent(), commentRequestDTO.isSpoiler());

        return toReponseDTO(comment);
    }

    /* [4] 댓글 삭제 (Soft Delete) */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        validateWriter(comment, userId);

        // visibilityStatus만 delete로 변경
        comment.delete();
    }

    // [5] 관리자용 댓글 전체 조회
    public List<CommentResponseDTO> getAllCommentsAdmin() {
        return commentRepository.findAll().stream()
                .map(this::toReponseDTO)
                .collect(Collectors.toList());
    }

    // [6] 관리자용 댓글 강제 삭제
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.delete(); // Soft delete
    }

    /* [ Helper Method ] */

    private void validateWriter(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    private CommentResponseDTO toReponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .nickname(comment.getUser().getUserInformation().getNickname())
                .content(comment.getContent())
                // 부모가 있으면 ID 반환, 없으면 null
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .spoiler(comment.isSpoiler())
                .isChanged(comment.isChanged())
                .status(comment.getVisibilityStatus())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
