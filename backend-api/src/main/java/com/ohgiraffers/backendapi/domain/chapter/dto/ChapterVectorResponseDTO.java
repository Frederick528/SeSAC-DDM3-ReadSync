package com.ohgiraffers.backendapi.domain.chapter.dto;

import lombok.Data;

@Data
public class ChapterVectorResponseDTO {
    private float[] embedding;

    // Getter, Setter 생략 (Lombok @Data 사용 권장)
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
}
