package com.ohgiraffers.backendapi.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeType {
    LIKE("좋아요"),
    DISLIKE("싫어요");

    private final String description;
}
