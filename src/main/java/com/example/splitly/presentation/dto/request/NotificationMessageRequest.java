package com.example.splitly.presentation.dto.request;

import java.util.List;
import java.util.Map;

import com.example.splitly.domain.enumerator.NotificationType;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMessageRequest {

    @NonNull
    private final NotificationType notificationType;

    @NonNull
    private final List<Integer> recipientUserIds;

    @NonNull
    private final String title;

    @NonNull
    private final String body;

    private final String notificationImage;

    @Builder.Default
    private final Map<String, String> payload = Map.of();
}
