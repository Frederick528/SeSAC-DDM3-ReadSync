package com.ohgiraffers.backendapi.domain.readingroom.dto;

import com.ohgiraffers.backendapi.domain.readingroom.enums.VoiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoomRequest {
    private Long libraryId;
    private String roomName;
    private VoiceType voiceType = VoiceType.BASIC;
    private Integer maxCapacity = 8;
    private Integer currentChapterId = 1;
}
