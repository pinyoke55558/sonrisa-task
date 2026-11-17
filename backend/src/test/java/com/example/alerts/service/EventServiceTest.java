package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.alerts.dto.CategoryResponse;
import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.PageResponse;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.CategoryMapper;
import com.example.alerts.mapper.EventMapper;
import com.example.alerts.model.Event;
import com.example.alerts.model.EventCategory;
import com.example.alerts.repository.EventRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private static final Instant OCCURRED = Instant.parse("2026-11-17T14:30:00Z");

    @Mock
    private EventRepository eventRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
        ReflectionTestUtils.setField(eventMapper, "categoryMapper", Mappers.getMapper(CategoryMapper.class));
        eventService = new EventService(eventMapper, eventRepository);
    }

    @Test
    void listReturnsRequestedPageWithPagingInfo() {
        PageRequest pageRequest = PageRequest.of(1, 2);
        when(eventRepository.findAllByDeletedFalseOrderByOccurredAtDesc(pageRequest))
                .thenReturn(new PageImpl<>(List.of(event(3L, "Newest"), event(2L, "Older")), pageRequest, 5));

        PageResponse<EventResponse> response = eventService.list(1, 2);

        assertThat(response.content()).extracting(EventResponse::title).containsExactly("Newest", "Older");
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
    }

    @Test
    void listReturnsEmptyPageWhenThereAreNoEvents() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        when(eventRepository.findAllByDeletedFalseOrderByOccurredAtDesc(pageRequest))
                .thenReturn(new PageImpl<>(List.of(), pageRequest, 0));

        PageResponse<EventResponse> response = eventService.list(0, 20);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    void loadReturnsEventWithCategory() {
        when(eventRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event(1L, "Quake")));

        EventResponse response = eventService.load(1L);

        assertThat(response.title()).isEqualTo("Quake");
        assertThat(response.occurredAt()).isEqualTo(OCCURRED);
        assertThat(response.category()).isEqualTo(new CategoryResponse(5L, "DUMMY_A", "Dummy A", null, 0L));
    }

    @Test
    void loadUnknownOrDeletedEventIsNotFound() {
        when(eventRepository.findByIdAndDeletedFalse(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.load(9L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Event 9 not found");
    }

    private static Event event(Long id, String title) {
        EventCategory category = new EventCategory();
        category.setId(5L);
        category.setCode("DUMMY_A");
        category.setName("Dummy A");
        category.setVersion(0L);

        Event event = new Event();
        event.setId(id);
        event.setCategory(category);
        event.setTitle(title);
        event.setSummary("Summary of " + title);
        event.setOccurredAt(OCCURRED);
        return event;
    }
}
