package com.example.alerts.repository;

import com.example.alerts.model.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = "category")
    Page<Event> findAllByDeletedFalseOrderByOccurredAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    List<Event> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Optional<Event> findByIdAndDeletedFalse(Long id);
}
