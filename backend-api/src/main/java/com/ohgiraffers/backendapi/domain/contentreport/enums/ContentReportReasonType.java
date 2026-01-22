package com.ohgiraffers.backendapi.domain.contentreport.enums;

public enum ContentReportReasonType {
    SPOILER("스포일러"),
    ABUSE("욕설비방"),
    ADVERTISEMENT("광고");

    private final String description;

    ContentReportReasonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
