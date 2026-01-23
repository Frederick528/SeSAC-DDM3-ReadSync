package com.ohgiraffers.backendapi.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BaseVectorEntity extends BaseTimeEntity {

    @Id
    private Long id;

    // Hibernate Vector 모듈이 'halfvec' <-> 'float[]' 자동 변환
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "halfvec(1024)")
    private float[] vector;

    public void updateVector(float[] vector) {
        this.vector = vector;
    }
}
