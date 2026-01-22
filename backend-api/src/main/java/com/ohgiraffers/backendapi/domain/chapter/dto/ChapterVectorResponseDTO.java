package com.ohgiraffers.backendapi.domain.chapter.dto;

import lombok.Data;

@Data
public class ChapterVectorResponseDTO {
    private float[] embedding;
}
