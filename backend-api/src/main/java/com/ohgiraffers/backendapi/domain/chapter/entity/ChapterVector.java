package com.ohgiraffers.backendapi.domain.chapter.entity;

import com.ohgiraffers.backendapi.global.common.BaseVectorEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "chapter_vectors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ChapterVector extends BaseVectorEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Chapter의 ID를 이 엔티티의 PK로 사용
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
}
