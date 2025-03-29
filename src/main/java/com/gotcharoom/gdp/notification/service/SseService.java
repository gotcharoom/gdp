package com.gotcharoom.gdp.notification.service;

import com.gotcharoom.gdp.global.util.UserUtil;
import com.gotcharoom.gdp.notification.entity.Notification;
import com.gotcharoom.gdp.notification.model.NotificationDto;
import com.gotcharoom.gdp.notification.model.NotificationType;
import com.gotcharoom.gdp.notification.repository.EmitterRepository;
import com.gotcharoom.gdp.notification.repository.NotificationRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SseService {

    // Timeout 1시간
    private final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final UserUtil userUtil;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseService(
            UserUtil userUtil,
            EmitterRepository emitterRepository,
            NotificationRepository notificationRepository
    ) {
        this.userUtil = userUtil;
        this.emitterRepository = emitterRepository;
        this.notificationRepository = notificationRepository;
    }

    public SseEmitter subscribe(String lastEventId) {
        GdpUser gdpUser = userUtil.getUserFromContext();
        Long memberId = gdpUser.getUid();

        log.info("Subscribing to notifications. MemberId: {}", memberId);

        String emitterId = memberId + "-" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> {
            emitterRepository.deleteById(emitterId);
            log.info("SSE emitter completed. EmitterId: {}", emitterId);
        });

        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
            log.info("SSE emitter timed out. EmitterId: {}", emitterId);
        });

        String initialContent = "Event Stream Created. [memberId=" + memberId + "]";
        Notification notification = Notification.createNotification(gdpUser, NotificationType.SYSTEM, initialContent, "", "Admin", false);

        sendToClient(emitter, emitterId, NotificationDto.from(notification));
        log.info("Sent initial system notification to memberId: {}", memberId);

        List<Notification> unreadNotifications = notificationRepository.findByReceiverAndIsReadFalse(gdpUser);
        log.info("Found {} unread notifications for memberId: {}", unreadNotifications.size(), memberId);

        unreadNotifications.forEach(unreadNotification -> {
            sendToClient(emitter, emitterId + "-" + unreadNotification.getId(), NotificationDto.from(unreadNotification));
            log.info("Sent unread notification. Id: {}", unreadNotification.getId());
        });

        if (!lastEventId.isEmpty()) {
            log.info("Checking for missed events since lastEventId: {}", lastEventId);
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> {
                        sendToClient(emitter, entry.getKey(), entry.getValue());
                        log.info("Resent missed event. EventId: {}", entry.getKey());
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
            log.info("Failed to send event to client. EmitterId: {}", emitterId);
            throw new RuntimeException("전송 실패");
        }
    }

    @Transactional
    protected void send(GdpUser receiver, NotificationType notificationType, String content, String url, String toName) {
        log.info("Sending notification to user. ReceiverId: {}, Type: {}, ToName: {}", receiver.getUid(), notificationType, toName);

        Notification newNotification = Notification.createNotification(receiver, notificationType, content, url, toName, false);
        Notification notification = notificationRepository.save(newNotification);

        String memberId = String.valueOf(receiver.getUid());
        Map<String, SseEmitter> sseEmitters = emitterRepository.fidAllEmitterStartWithByMemberId(memberId);

        log.info("Found {} SSE emitters for memberId: {}", sseEmitters.size(), memberId);

        sseEmitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendToClient(emitter, key, NotificationDto.from(notification));
            log.info("Notification sent through SSE. NotificationId: {}, EmitterKey: {}", notification.getId(), key);
        });
    }
}
