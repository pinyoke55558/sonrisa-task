package com.example.alerts.repository;

import com.example.alerts.model.AlertRule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    /** A user's own rules. */
    List<AlertRule> findAllByOwnerIdAndDeletedFalseOrderByNameAsc(Long ownerId);

    /** A single rule, only if it belongs to the given user (others' rules answer 404). */
    Optional<AlertRule> findByIdAndOwnerIdAndDeletedFalse(Long id, Long ownerId);

    /** All users' rules, for the admin read-only view. */
    List<AlertRule> findAllByDeletedFalseOrderByNameAsc();
}
