package com.ohgiraffers.backendapi.domain.chapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChapterJsonDTO {
    private List<ContentNode> content;

    @Data
    public static class ContentNode {
        private String text;
        private String type; // paragraph, quote 등
    }
}
