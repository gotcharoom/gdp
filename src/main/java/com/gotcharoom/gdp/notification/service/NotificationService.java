package com.gotcharoom.gdp.notification.service;

import com.gotcharoom.gdp.notification.entity.Notification;
import com.gotcharoom.gdp.notification.model.NotificationReadRequest;
import com.gotcharoom.gdp.notification.model.NotificationSendRequest;
import com.gotcharoom.gdp.notification.repository.NotificationRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class NotificationService {

    private final SseService sseService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public NotificationService(
            SseService sseService,
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.sseService = sseService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public void sendRequest(NotificationSendRequest request) {
        log.info("Sending notification via request. TargetMemberId: {}", request.getMemberId());

        GdpUser gdpUser = userRepository.findById(request.getMemberId()).orElseThrow();
        sseService.send(gdpUser, request.getNotificationType(), request.getContent(), request.getUrl(), request.getToName());

        log.info("Notification request completed. TargetMemberId: {}", request.getMemberId());
    }

    @Transactional
    public void readNotification(NotificationReadRequest request) {
        log.info("Marking notification as read. NotificationId: {}", request.getNotificationId());

        Notification notification = notificationRepository.findById(request.getNotificationId()).orElseThrow();
        Notification updatedNotification = notification.readNotification(request.getNotificationId());
        notificationRepository.save(updatedNotification);

        log.info("Notification marked as read. NotificationId: {}", request.getNotificationId());
    }
}
