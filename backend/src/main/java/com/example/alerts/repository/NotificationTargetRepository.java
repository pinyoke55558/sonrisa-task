package com.example.alerts.repository;

import com.example.alerts.model.NotificationTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {
}
