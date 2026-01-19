package com.ohgiraffers.backendapi.domain.comment.entity;

import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentEntityTest {

    @Test
    @DisplayName("댓글 내용을 수정하면 내용이 바뀌고 isChanged가 true가 된다.")
    void updateContent_Test() {
        // given
        Comment comment = Comment.builder()
                .content("원본 내용")
                .isSpoiler(false)
                .build();

        // when
        comment.updateContent("수정된 내용", true);

        // then
        assertThat(comment.getContent()).isEqualTo("수정된 내용");
        assertThat(comment.isChanged()).isTrue();
    }

    @Test
    @DisplayName("같은 내용으로 수정 시도하면 isChanged는 변하지 않는다.")
    void updateContent_SameContent_Test() {
        // given
        Comment comment = Comment.builder()
                .content("원본 내용")
                .isSpoiler(false)
                .build();

        // when
        comment.updateContent("원본 내용", false);

        // then
        assertThat(comment.isChanged()).isFalse(); // 그대로 false여야 함
    }

    @Test
    @DisplayName("delete() 호출 시 상태가 DELETED로 변경된다.")
    void delete_Test() {
        // given
        Comment comment = Comment.builder()
                .content("삭제될 댓글")
                .build();

        // when
        comment.delete();

        // then
        assertThat(comment.getVisibilityStatus()).isEqualTo(VisibilityStatus.DELETED);
    }
}