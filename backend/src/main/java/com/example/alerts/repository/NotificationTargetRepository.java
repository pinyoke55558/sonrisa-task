package com.example.alerts.repository;

import com.example.alerts.model.NotificationTarget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {

    List<NotificationTarget> findAllByRuleIdAndDeletedFalse(Long ruleId);
}
