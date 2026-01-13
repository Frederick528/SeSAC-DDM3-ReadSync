package com.ohgiraffers.backendapi.domain.comment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentReportTargetType {
    BOOKCOMMEMT("책댓글"),
    REVIEW("리뷰");

    private final String description;
}
