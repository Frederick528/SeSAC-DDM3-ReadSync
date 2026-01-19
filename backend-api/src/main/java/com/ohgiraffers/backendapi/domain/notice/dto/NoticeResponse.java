package com.ohgiraffers.backendapi.domain.notice.dto;

import com.ohgiraffers.backendapi.domain.notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NoticeResponse {

    private Long noticeId;
    private String title;
    private String content;
    private Long adminId;
    private int views;
    private LocalDateTime createdAt;

    /** Entity → Response 변환 */
    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .adminId(notice.getAdminId())
                .views(notice.getViews())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
