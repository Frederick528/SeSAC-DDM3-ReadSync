package com.ohgiraffers.backendapi.domain.notice.dto;

import com.ohgiraffers.backendapi.domain.notice.entity.Notice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponse {

    private Long noticeId;
    private String title;
    private String content;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .build();
    }
}
