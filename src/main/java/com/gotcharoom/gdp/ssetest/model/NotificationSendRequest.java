package com.gotcharoom.gdp.ssetest.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendRequest {
    @NotNull
    private Long memberId;
    @NotNull
    private NotificationType notificationType;
    @NotNull
    private String content;
    private String url;
    @NotNull
    private String toName;
}
