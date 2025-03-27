package com.gotcharoom.gdp.notification.repository;

import com.gotcharoom.gdp.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByIdAndIsReadFalse(Long id);
}
