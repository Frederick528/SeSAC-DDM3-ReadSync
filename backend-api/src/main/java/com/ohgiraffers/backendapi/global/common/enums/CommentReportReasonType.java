package com.ohgiraffers.backendapi.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentReportReasonType {
    SPOILER("스포일러"),
    ABUSE("욕설비방"),
    ADVERTISEMENT("광고");

    private final String description;
}


