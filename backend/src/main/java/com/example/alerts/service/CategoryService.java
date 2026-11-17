package com.example.alerts.service;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.CategoryMapper;
import com.example.alerts.repository.EventCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

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
}
