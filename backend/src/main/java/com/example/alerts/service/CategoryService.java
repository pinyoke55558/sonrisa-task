package com.example.alerts.service;

import static com.example.alerts.exception.ExceptionUtil.duplicateCode;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.dto.CategorySaveRequest;
import com.example.alerts.dto.CategoryUpdateRequest;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.CategoryMapper;
import com.example.alerts.model.EventCategory;
import com.example.alerts.repository.EventCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private static final String CATEGORY = "category";

    private final EventCategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> list() {
        return categoryRepository.findAllByDeletedFalseOrderByNameAsc().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse load(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    public EventCategory loadEntity(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    @Transactional
    public CategoryResponse save(CategorySaveRequest request) {
        if (categoryRepository.existsByCodeAndDeletedFalse(request.code())) {
            throw duplicateCode(CATEGORY, request.code());
        }

        return categoryMapper.toResponse(categoryRepository.saveAndFlush(categoryMapper.toEntity(request)));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        if (!categoryRepository.existsByIdAndDeletedFalse(id)) {
            throw new NotFoundException("Category", id);
        }

        if (categoryRepository.existsByCodeAndDeletedFalseAndIdNot(request.code(), id)) {
            throw duplicateCode(CATEGORY, request.code());
        }

        EventCategory category = categoryMapper.toEntity(id, request);
        return categoryMapper.toResponse(categoryRepository.saveAndFlush(category));
    }

    @Transactional
    public void delete(Long id) {
        EventCategory category = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Category", id));

        category.setDeleted(true);
        categoryRepository.saveAndFlush(category);
    }
}
