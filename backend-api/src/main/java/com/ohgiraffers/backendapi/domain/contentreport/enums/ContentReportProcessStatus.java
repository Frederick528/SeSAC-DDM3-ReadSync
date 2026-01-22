package com.ohgiraffers.backendapi.domain.contentreport.enums;

public enum ContentReportProcessStatus {
    PENDING("대기"),
    ACCEPTED("승인"),
    REJECTED("반려");

    private final String description;

    ContentReportProcessStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
