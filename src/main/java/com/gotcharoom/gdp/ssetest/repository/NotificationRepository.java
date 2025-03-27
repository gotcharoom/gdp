package com.gotcharoom.gdp.ssetest.repository;

import com.gotcharoom.gdp.ssetest.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
