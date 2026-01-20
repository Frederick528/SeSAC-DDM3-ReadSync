package com.ohgiraffers.backendapi.domain.bookmark.entity;

import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Entity
@Table(name = "bookmarks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false)
    private Integer lastReadPos;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal progress;

    // @Lob // <--- 이 부분을 삭제하거나 주석 처리하세요!
    @JdbcType(BinaryJdbcType.class) // JDBC 타입을 명시적으로 이진 데이터로 지정
    @Column(nullable = false)
    private byte[] readMask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    public void syncReadStatus(List<Integer> newIndices, Integer lastReadPos) {
        String updatedMask = getString(newIndices);
        this.readMask = updatedMask.getBytes(StandardCharsets.UTF_8);
        this.lastReadPos = lastReadPos;

        // 2. 진행률 계산
        long readCount = updatedMask.chars().filter(ch -> ch == '1').count();
        this.progress = BigDecimal.valueOf(readCount)
                .divide(BigDecimal.valueOf(chapter.getSequence()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String getString(List<Integer> newIndices) {
        StringBuilder sb = new StringBuilder(new String(this.readMask, StandardCharsets.UTF_8));

        // 1. 마스크 업데이트
        for (Integer index : newIndices) {
            // 1. 유효성 검사: 1보다 작거나, 최대값(totalLength)보다 크면 예외 발생
            if (index < 1 || index > chapter.getSequence()) {
                throw new IllegalArgumentException(
                        String.format("잘못된 문단 번호입니다: %d (허용 범위: 1 ~ %d)", index, chapter.getSequence())
                );
            }

            // 2. 인덱스 보정: 사용자가 입력한 1~10을 0~9로 변환
            int setIndex = index - 1;
            sb.setCharAt(setIndex, '1');
        }

        return sb.toString();
    }
}