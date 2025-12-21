package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.presentation.dto.request.NotificationMessageRequest;
import com.example.splitly.presentation.dto.response.UserNotificationResponse;

public interface INotificationService {
    public void sendNotification(NotificationMessageRequest notificationMessageRequest);
    public List<UserNotificationResponse> getAllNotifications(Integer userId);
}
