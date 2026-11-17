package com.example.alerts.repository;

import com.example.alerts.model.AlertRule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    List<AlertRule> findAllByOwnerIdAndDeletedFalseOrderByNameAsc(Long ownerId);

    Optional<AlertRule> findByIdAndOwnerIdAndDeletedFalse(Long id, Long ownerId);

    boolean existsByIdAndOwnerIdAndDeletedFalse(Long id, Long ownerId);

    List<AlertRule> findAllByDeletedFalseOrderByNameAsc();

    Optional<AlertRule> findByIdAndDeletedFalse(Long id);

    List<AlertRule> findAllByEnabledTrueAndDeletedFalse();
}
