package com.ohgiraffers.backendapi.domain.book.entity;

import com.ohgiraffers.backendapi.global.common.BaseVectorEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "book_vectors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BookVector extends BaseVectorEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Book의 ID를 이 엔티티의 PK로 사용
    @JoinColumn(name = "book_id")
    private Book book;
}
