package com.ohgiraffers.backendapi.domain.contentreport.enums;

public enum ContentReportTargetType {
    CHAPTERS_COMMENT("챕터댓글"),
    REVIEW("리뷰");

    private final String description;

    ContentReportTargetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
