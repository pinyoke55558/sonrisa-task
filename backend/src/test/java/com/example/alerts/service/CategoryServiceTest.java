package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.dto.CategorySaveRequest;
import com.example.alerts.dto.CategoryUpdateRequest;
import com.example.alerts.exception.ConflictException;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.CategoryMapper;
import com.example.alerts.model.EventCategory;
import com.example.alerts.repository.EventCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private EventCategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);
        categoryService = new CategoryService(categoryRepository, mapper);
    }

    @Test
    void listReturnsNonDeletedCategoriesInRepositoryOrder() {
        when(categoryRepository.findAllByDeletedFalseOrderByNameAsc()).thenReturn(List.of(
                category(2L, "DUMMY_A", "Alpha", 0L),
                category(1L, "DUMMY_B", "Beta", 1L)));

        List<CategoryResponse> responses = categoryService.list();

        assertThat(responses).containsExactly(
                new CategoryResponse(2L, "DUMMY_A", "Alpha", null, 0L),
                new CategoryResponse(1L, "DUMMY_B", "Beta", null, 1L));
    }

    @Test
    void listReturnsEmptyListWhenThereAreNoCategories() {
        when(categoryRepository.findAllByDeletedFalseOrderByNameAsc()).thenReturn(List.of());

        assertThat(categoryService.list()).isEmpty();
    }

    @Test
    void loadReturnsCategory() {
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(category(1L, "DUMMY_A", "Alpha", 4L)));

        CategoryResponse response = categoryService.load(1L);

        assertThat(response).isEqualTo(new CategoryResponse(1L, "DUMMY_A", "Alpha", null, 4L));
    }

    @Test
    void loadUnknownOrDeletedCategoryIsNotFound() {
        when(categoryRepository.findByIdAndDeletedFalse(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.load(9L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category 9 not found");
    }

    @Test
    void saveStoresNewCategory() {
        when(categoryRepository.existsByCodeAndDeletedFalse("DUMMY_A")).thenReturn(false);
        when(categoryRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            EventCategory saved = invocation.getArgument(0);
            saved.setId(1L);
            saved.setVersion(0L);
            return saved;
        });

        CategoryResponse response = categoryService.save(new CategorySaveRequest("DUMMY_A", "Dummy A", "Test"));

        assertThat(response).isEqualTo(new CategoryResponse(1L, "DUMMY_A", "Dummy A", "Test", 0L));
    }

    @Test
    void saveRejectsDuplicateCode() {
        when(categoryRepository.existsByCodeAndDeletedFalse("DUMMY_A")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.save(new CategorySaveRequest("DUMMY_A", "Dummy A", null)))
                .isInstanceOf(ConflictException.class);
        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void updatePassesClientVersionToJpa() {
        when(categoryRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);
        when(categoryRepository.existsByCodeAndDeletedFalseAndIdNot("DUMMY_A", 1L)).thenReturn(false);
        when(categoryRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.update(1L, new CategoryUpdateRequest("DUMMY_A", "New name", null, 3L));

        ArgumentCaptor<EventCategory> saved = ArgumentCaptor.forClass(EventCategory.class);
        verify(categoryRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getId()).isEqualTo(1L);
        assertThat(saved.getValue().getVersion()).isEqualTo(3L);
        assertThat(saved.getValue().getName()).isEqualTo("New name");
    }

    @Test
    void updateRejectsCodeOfAnotherCategory() {
        when(categoryRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);
        when(categoryRepository.existsByCodeAndDeletedFalseAndIdNot("DUMMY_B", 1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(1L, new CategoryUpdateRequest("DUMMY_B", "Name", null, 0L)))
                .isInstanceOf(ConflictException.class);
        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateUnknownOrDeletedCategoryIsNotFound() {
        when(categoryRepository.existsByIdAndDeletedFalse(9L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.update(9L, new CategoryUpdateRequest("X", "X", null, 0L)))
                .isInstanceOf(NotFoundException.class);
        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteMarksCategoryAsDeleted() {
        EventCategory existing = category(1L, "DUMMY_A", "Name", 2L);
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));

        categoryService.delete(1L);

        assertThat(existing.isDeleted()).isTrue();
        verify(categoryRepository).saveAndFlush(existing);
        verify(categoryRepository, never()).delete(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteUnknownOrAlreadyDeletedCategoryIsNotFound() {
        when(categoryRepository.findByIdAndDeletedFalse(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(9L)).isInstanceOf(NotFoundException.class);
        verify(categoryRepository, never()).saveAndFlush(any());
    }

    private static EventCategory category(Long id, String code, String name, Long version) {
        EventCategory category = new EventCategory();
        category.setId(id);
        category.setCode(code);
        category.setName(name);
        category.setVersion(version);
        return category;
    }
}
