package com.example.alerts.repository;

import com.example.alerts.model.EventCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    List<EventCategory> findAllByDeletedFalseOrderByNameAsc();

    Optional<EventCategory> findByIdAndDeletedFalse(Long id);

    boolean existsByIdAndDeletedFalse(Long id);

    boolean existsByCodeAndDeletedFalse(String code);

    boolean existsByCodeAndDeletedFalseAndIdNot(String code, Long id);
}
