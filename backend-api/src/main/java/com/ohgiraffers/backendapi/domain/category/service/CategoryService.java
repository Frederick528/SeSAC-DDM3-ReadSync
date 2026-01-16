package com.ohgiraffers.backendapi.domain.category.service;

import com.ohgiraffers.backendapi.domain.category.dto.CategoryRequestDTO;
import com.ohgiraffers.backendapi.domain.category.dto.CategoryResponseDTO;
import com.ohgiraffers.backendapi.domain.category.entity.Category;
import com.ohgiraffers.backendapi.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 1. 카테고리 등록
    @Transactional
    public Long createCategory(CategoryRequestDTO request) {
        Category category = request.toEntity();

        return categoryRepository.save(category).getCategoryId();
    }

    // 2. 카테고리 단건 조회
    public CategoryResponseDTO getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
        return CategoryResponseDTO.from(category);
    }

    // 3. 카테고리 전체 조회
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAllByDeletedAtIsNull().stream()
                .map(CategoryResponseDTO::from)
                .toList();
    }

    // 4. 카테고리 수정 (수정 시 더티 체킹 활용을 위해 @Transactional 필수)
    @Transactional
    public void updateCategory(Long categoryId, CategoryRequestDTO request) {
        // 1. 수정할 카테고리 조회 (삭제되지 않은 것만)
        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("수정할 카테고리가 존재하지 않거나 이미 삭제되었습니다."));

        // 2. 엔티티의 비즈니스 메서드 호출 (변경 감지 활용)
        category.update(request);
    }

    // 5. 카테고리 삭제 (Soft Delete)
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("이미 존재하지 않는 카테고리입니다."));
        category.delete(); // BaseTimeEntity의 Soft Delete 로직 호출
    }
}