package com.gotcharoom.gdp.ssetest.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.ssetest.model.NotificationSendRequest;
import com.gotcharoom.gdp.ssetest.service.NotificationTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse-test")
@Tag(name = "SSE 테스트용", description = "SSE 테스트 컨트롤러")
public class NotificationTestController {

    private final NotificationTestService notificationTestService;

    public NotificationTestController(NotificationTestService notificationTestService) {
        this.notificationTestService = notificationTestService;
    }

    @Operation(
            summary = "SSE 테스트",
            description = "SSE 테스트 API"
    )
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connectNotification(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        SseEmitter response = notificationTestService.subscribe(lastEventId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "SSE 테스트",
            description = "SSE 테스트 API"
    )
    @PostMapping(value = "/send")
    public ApiResponse<Void> sendTest(@RequestBody NotificationSendRequest request) {

        notificationTestService.sendRequest(request);

        return ApiResponse.success();
    }
}
