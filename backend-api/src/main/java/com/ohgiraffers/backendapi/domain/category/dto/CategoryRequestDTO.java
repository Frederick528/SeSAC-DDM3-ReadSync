package com.ohgiraffers.backendapi.domain.category.dto;

import com.ohgiraffers.backendapi.domain.book.enums.ViewPermission;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {
    private String categoryName;
    private Integer expByCategory;
}
