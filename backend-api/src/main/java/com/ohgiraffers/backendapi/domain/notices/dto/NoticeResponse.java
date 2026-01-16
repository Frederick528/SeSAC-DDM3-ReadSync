package com.ohgiraffers.backendapi.domain.notices.dto;

import com.ohgiraffers.backendapi.domain.notices.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NoticeResponse {

    @Getter
    @Builder
    public static class Detail {
        private Long noticeId;
        private String title;
        private String content;
        private int views;
        private Long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Detail from(Notice notice) {
            return Detail.builder()
                    .noticeId(notice.getNoticeId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .views(notice.getViews())
                    .userId(notice.getUser().getId())
                    .createdAt(notice.getCreatedAt())
                    .updatedAt(notice.getUpdatedAt())
                    .build();
        }
    }
}
