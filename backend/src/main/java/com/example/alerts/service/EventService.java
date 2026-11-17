package com.example.alerts.service;

import com.example.alerts.dto.EventResponse;
import com.example.alerts.dto.PageResponse;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.EventMapper;
import com.example.alerts.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    public PageResponse<EventResponse> list(int page, int size) {
        return PageResponse.of(
                eventRepository.findAllByDeletedFalseOrderByOccurredAtDesc(PageRequest.of(page, size)),
                eventMapper::toResponse);
    }

    public EventResponse load(Long id) {
        return eventRepository.findByIdAndDeletedFalse(id)
                .map(eventMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Event", id));
    }
}
