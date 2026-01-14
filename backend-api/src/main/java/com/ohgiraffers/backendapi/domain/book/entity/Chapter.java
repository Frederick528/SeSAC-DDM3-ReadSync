package com.ohgiraffers.backendapi.domain.book.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chapters")
public class Chapter extends BaseTimeEntity{
    @Id
    @Column(name = "chapter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book bookId;
    @Column(name = "chapter_name", length = 255)
    private String chapterName;
    @Column(name = "sequence", nullable = false)
    private Integer sequence = 1;
    @Column(name = "book_content_path", nullable = false)
    private String bookContentPath;
    @Column(name = "is_embedded", nullable = false)
    private Boolean isEmbedded = false;
    @Column(name = "embedding_model", length = 255)
    private String embeddingModel;
    // ※주의※ : Java의 List<Double>과 PostgreSQL의 VECTOR 타입은 자동으로 매핑되지 않는 경우가 많습니다.
    // 해결책: 만약 실행 시 에러가 난다면, hibernate-vector 라이브러리를 의존성에 추가하고 아래 어노테이션을 붙여야 합니다.
    @JdbcTypeCode(SqlTypes.VECTOR) // 이 부분이 필요할 수 있음
    @Column(name = "vector", columnDefinition = "VECTOR(1536)")
    private List<Double> vector;

    @Builder
    public Chapter(Book bookId, String chapterName, Integer sequence, String bookContentPath) {
        this.bookId = bookId;
        this.chapterName = chapterName;
        this.sequence = sequence != null ? sequence : 1;
        this.bookContentPath = bookContentPath;
        this.isEmbedded = isEmbedded;
    }
}
