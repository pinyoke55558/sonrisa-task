package com.example.alerts.repository;

import com.example.alerts.model.Event;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    /** Paged events list, newest first (API design §3.2). */
    Page<Event> findAllByDeletedFalseOrderByOccurredAtDesc(Pageable pageable);

    Optional<Event> findByIdAndDeletedFalse(Long id);
}
