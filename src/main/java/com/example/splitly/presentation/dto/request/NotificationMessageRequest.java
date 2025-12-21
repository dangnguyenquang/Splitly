package com.example.splitly.presentation.dto.request;

import java.util.List;
import java.util.Map;

import com.example.splitly.domain.enumerator.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationMessageRequest {

    private NotificationType notificationType;
    private List<Integer> recipientUserIds;
    private String title;
    private String body;
    private String notificationImage;
    private Map<String, String> payload;
}
