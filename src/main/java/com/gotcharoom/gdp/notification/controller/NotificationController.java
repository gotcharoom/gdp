package com.gotcharoom.gdp.notification.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.notification.model.NotificationSendRequest;
import com.gotcharoom.gdp.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse")
@Tag(name = "SSE 알람", description = "SSE 알람 컨트롤러")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "SSE 알람 Connection",
            description = "SSE 알람 Connection API"
    )
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connectNotification(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        SseEmitter response = notificationService.subscribe(lastEventId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "SSE 알람 전송",
            description = "SSE 알람 전송 API"
    )
    @PostMapping(value = "/send")
    public ResponseEntity<Void> sendMessage(@RequestBody NotificationSendRequest request) {

        notificationService.sendRequest(request);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "SSE 알림 읽음 처리",
            description = "SSE 알림 읽음 처리 API"
    )
    @PutMapping(value = "/read")
    public ApiResponse<Void> readMessage(@RequestParam Long notificationId) {

        notificationService.readNotification(notificationId);

        return ApiResponse.success();
    }
}
