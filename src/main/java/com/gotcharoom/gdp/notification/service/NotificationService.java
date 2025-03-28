package com.gotcharoom.gdp.notification.service;

import com.gotcharoom.gdp.global.util.UserUtil;
import com.gotcharoom.gdp.notification.entity.Notification;
import com.gotcharoom.gdp.notification.model.NotificationDto;
import com.gotcharoom.gdp.notification.model.NotificationReadRequest;
import com.gotcharoom.gdp.notification.model.NotificationSendRequest;
import com.gotcharoom.gdp.notification.model.NotificationType;
import com.gotcharoom.gdp.notification.repository.EmitterRepository;
import com.gotcharoom.gdp.notification.repository.NotificationRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // Timeout 1시간
    private final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final UserUtil userUtil;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            UserUtil userUtil,
            EmitterRepository emitterRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.userUtil = userUtil;
        this.emitterRepository = emitterRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public SseEmitter subscribe(String lastEventId) {
        GdpUser gdpUser = userUtil.getUserFromContext();
        Long memberId = gdpUser.getUid();

        logger.info("Subscribing to notifications. MemberId: {}", memberId);

        String emitterId = memberId + "-" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> {
            emitterRepository.deleteById(emitterId);
            logger.info("SSE emitter completed. EmitterId: {}", emitterId);
        });

        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
            logger.info("SSE emitter timed out. EmitterId: {}", emitterId);
        });

        String initialContent = "Event Stream Created. [memberId=" + memberId + "]";
        Notification notification = Notification.createNotification(gdpUser, NotificationType.SYSTEM, initialContent, "", "Admin", false);

        sendToClient(emitter, emitterId, NotificationDto.from(notification));
        logger.info("Sent initial system notification to memberId: {}", memberId);

        List<Notification> unreadNotifications = notificationRepository.findByReceiverAndIsReadFalse(gdpUser);
        logger.info("Found {} unread notifications for memberId: {}", unreadNotifications.size(), memberId);

        unreadNotifications.forEach(unreadNotification -> {
            sendToClient(emitter, emitterId + "-" + unreadNotification.getId(), NotificationDto.from(unreadNotification));
            logger.info("Sent unread notification. Id: {}", unreadNotification.getId());
        });

        if (!lastEventId.isEmpty()) {
            logger.info("Checking for missed events since lastEventId: {}", lastEventId);
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> {
                        sendToClient(emitter, entry.getKey(), entry.getValue());
                        logger.info("Resent missed event. EventId: {}", entry.getKey());
                    });
        }

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            logger.info("Failed to send event to client. EmitterId: {}", emitterId);
            throw new RuntimeException("전송 실패");
        }
    }

    public void send(GdpUser receiver, NotificationType notificationType, String content, String url, String toName) {
        logger.info("Sending notification to user. ReceiverId: {}, Type: {}, ToName: {}", receiver.getUid(), notificationType, toName);

        Notification newNotification = Notification.createNotification(receiver, notificationType, content, url, toName, false);
        Notification notification = notificationRepository.save(newNotification);

        String memberId = String.valueOf(receiver.getUid());
        Map<String, SseEmitter> sseEmitters = emitterRepository.fidAllEmitterStartWithByMemberId(memberId);

        logger.info("Found {} SSE emitters for memberId: {}", sseEmitters.size(), memberId);

        sseEmitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendToClient(emitter, key, NotificationDto.from(notification));
            logger.info("Notification sent through SSE. NotificationId: {}, EmitterKey: {}", notification.getId(), key);
        });
    }

    public void sendRequest(NotificationSendRequest request) {
        logger.info("Sending notification via request. TargetMemberId: {}", request.getMemberId());

        GdpUser gdpUser = userRepository.findById(request.getMemberId()).orElseThrow();
        send(gdpUser, request.getNotificationType(), request.getContent(), request.getUrl(), request.getToName());

        logger.info("Notification request completed. TargetMemberId: {}", request.getMemberId());
    }

    public void readNotification(NotificationReadRequest request) {
        logger.info("Marking notification as read. NotificationId: {}", request.getNotificationId());

        Notification notification = notificationRepository.findById(request.getNotificationId()).orElseThrow();
        Notification updatedNotification = notification.readNotification(request.getNotificationId());
        notificationRepository.save(updatedNotification);

        logger.info("Notification marked as read. NotificationId: {}", request.getNotificationId());
    }
}
