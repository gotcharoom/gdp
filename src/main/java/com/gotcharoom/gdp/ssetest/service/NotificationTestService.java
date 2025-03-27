package com.gotcharoom.gdp.ssetest.service;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.util.UserUtil;
import com.gotcharoom.gdp.ssetest.entity.Notification;
import com.gotcharoom.gdp.ssetest.model.NotificationDto;
import com.gotcharoom.gdp.ssetest.model.NotificationType;
import com.gotcharoom.gdp.ssetest.repository.EmitterRepository;
import com.gotcharoom.gdp.ssetest.repository.NotificationRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

@Service
@Transactional
public class NotificationTestService {

    // Timeout 1시간
    private final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final UserUtil userUtil;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationTestService(
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

        String emitterId = memberId + "-" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "Event Stream Created. [memberId=" + memberId + "]");

        if(!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
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
            throw new RuntimeException("전송 실패");
        }
    }

    public void send(GdpUser receiver, NotificationType notificationType, String content, String url, String toName) {
        Notification newNotification = Notification.createNotification(receiver, notificationType, content, url, toName, false);
        Notification notification = notificationRepository.save(newNotification);

        String memberId = String.valueOf(receiver.getUid());

        Map<String, SseEmitter> sseEmitters = emitterRepository.fidAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            // Entity는 직렬화가 안되기 때문에 별도의 Dto 만들어주어야 함
            sendToClient(emitter, key, ApiResponse.success(NotificationDto.from(notification)));
        });
    }

    public void sendTest() {
        GdpUser gdpUser = userUtil.getUserFromContext();
        send(gdpUser, NotificationType.COMMENT, "테스트입니다", "/test/url", "Admin");
    }
}
