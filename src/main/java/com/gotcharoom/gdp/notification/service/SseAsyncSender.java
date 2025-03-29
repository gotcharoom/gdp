package com.gotcharoom.gdp.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class SseAsyncSender {
    @Async
    protected void send(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event().id(emitterId).data(data));
        } catch (IOException exception) {
            throw new RuntimeException("SSE 전송 실패", exception);
        }
    }
}
