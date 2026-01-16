package com.ohgiraffers.backendapi.domain.category.entity;


import com.ohgiraffers.backendapi.domain.book.enums.ViewPermission;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "categories")
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Integer expByCategory;

    public void update(String categoryName, Integer expByCategory) {
        if (categoryName != null && !categoryName.isBlank()) {
            this.categoryName = categoryName;
        }
        if (expByCategory != null) {
            this.expByCategory = expByCategory;
        }
    }

}
