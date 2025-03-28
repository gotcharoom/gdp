package com.gotcharoom.gdp.notification.entity;

import com.gotcharoom.gdp.notification.model.NotificationType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/* Extend Auditable 고려 */
@Entity(name="notification")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "receiver")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    // @Embedded -> entity안에 아래 객체의 속성을 포함시킴(Embedded)
    @Embedded
    private NotificationContent content;
    */

    @Column(name = "content")
    private String content;

    @Column(name = "url")
    private String url;

    @Column(name = "to_name")
    private String toName;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(name="notification_type", nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private GdpUser receiver;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public static Notification createNotification(GdpUser receiver, NotificationType notificationType, String content, String url, String toName, Boolean isRead) {
        return Notification.builder()
                .content(content)
                .url(url)
                .toName(toName)
                .isRead(isRead)
                .notificationType(notificationType)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Notification readNotification(Long id) {
        return this.toBuilder()
                .isRead(true)
                .build();
    }
}
