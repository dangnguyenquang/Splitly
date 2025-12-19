package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.DeviceToken;
import com.example.splitly.domain.repository.DeviceTokenRepository;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseMessaging messaging;
    private final DeviceTokenRepository tokenRepo;

    private final IUserService iUserService;

    @Transactional
    public void sendToUser(NotificationMessageRequest notificationMessageRequest) {
        Integer userId = iUserService.getCurrentUser().getUserId();
        var tokens = tokenRepo.findByUserIdAndActiveTrue(userId)
                .stream().map(DeviceToken::getToken).toList();

        if (tokens.isEmpty())
            return;

        Notification notification = Notification.builder()
                .setBody(notificationMessageRequest.getBody())
                .setTitle(notificationMessageRequest.getTitle())
                .build();

        for (var batch : chunk(tokens, 500)) {
            MulticastMessage msg = MulticastMessage.builder()
                    .setNotification(notification)
                    .putAllData(notificationMessageRequest.getData()) // data-only
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .addAllTokens(batch)
                    .build();

            BatchResponse res;
            try {
                res = messaging.sendEachForMulticast(msg);
                System.out.println("tokens=" + tokens.size());
                System.out.println("success=" + res.getSuccessCount() + ", failure=" + res.getFailureCount());
                res.getResponses().forEach(r -> {
                    if (!r.isSuccessful())
                        System.out.println("FCM err=" + r.getException().getMessage());
                });

            } catch (FirebaseMessagingException e) {
                throw new RuntimeException("FCM send failed", e);
            }

            // cleanup token chết (đơn giản: disable)
            for (int i = 0; i < res.getResponses().size(); i++) {
                var r = res.getResponses().get(i);
                if (!r.isSuccessful()) {
                    String badToken = batch.get(i);
                    tokenRepo.findByToken(badToken).ifPresent(t -> {
                        t.setActive(false);
                        t.setUpdatedAt(LocalDateTime.now());
                        tokenRepo.save(t);
                    });
                }
            }
        }
    }

    private static <T> List<List<T>> chunk(List<T> xs, int size) {
        List<List<T>> out = new ArrayList<>();
        for (int i = 0; i < xs.size(); i += size) {
            out.add(xs.subList(i, Math.min(i + size, xs.size())));
        }
        return out;
    }
}
