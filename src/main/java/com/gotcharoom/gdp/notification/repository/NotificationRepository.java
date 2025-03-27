package com.gotcharoom.gdp.notification.repository;

import com.gotcharoom.gdp.notification.entity.Notification;
import com.gotcharoom.gdp.user.entity.GdpUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverAndIsReadFalse(GdpUser user);
}
