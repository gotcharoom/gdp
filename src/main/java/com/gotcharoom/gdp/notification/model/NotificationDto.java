package com.gotcharoom.gdp.notification.model;

import com.gotcharoom.gdp.notification.entity.Notification;

public record NotificationDto(
        Long id,
        String content,
        String url,
        String toName,
        Boolean isRead,
        NotificationType notificationType
) {
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getContent(),
                notification.getUrl(),
                notification.getToName(),
                notification.getIsRead(),
                notification.getNotificationType()
        );
    }
}

