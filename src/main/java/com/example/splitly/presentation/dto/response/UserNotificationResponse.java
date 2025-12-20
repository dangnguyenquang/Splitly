package com.example.splitly.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.splitly.domain.entity.NotificationEvent;
import com.example.splitly.domain.enumerator.EventStatus;
import com.example.splitly.domain.enumerator.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {
    // recipient row
    private Long recipientRowId;        // notification_recipients.id
    private Integer recipientUserId;
    private LocalDateTime readAt;
    private EventStatus eventStatus;

    // event info
    private Integer actorUserId;
    private Long eventId;             
    private String title;
    private String body;
    private String notificationImage;
    private Map<String, String> payload;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
     
}
