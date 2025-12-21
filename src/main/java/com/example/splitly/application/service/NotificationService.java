package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.splitly.application.serviceInterface.INotificationService;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.DeviceToken;
import com.example.splitly.domain.entity.NotificationEvent;
import com.example.splitly.domain.entity.NotificationRecipient;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.EventStatus;
import com.example.splitly.domain.repository.DeviceTokenRepository;
import com.example.splitly.domain.repository.NotificationEventRepository;
import com.example.splitly.domain.repository.NotificationRecipientRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;
import com.example.splitly.presentation.dto.response.UserNotificationResponse;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final FirebaseMessaging messaging;
    private final DeviceTokenRepository tokenRepo;
    private final NotificationEventRepository eventRepo;
    private final NotificationRecipientRepository recipientRepo;
    private final IUserService iUserService;
    private final UserRepository userRepo;

    public void sendNotification(NotificationMessageRequest notificationMessageRequest) {
        List<Integer> recipientUserIds = notificationMessageRequest.getRecipientUserIds() == null ? List.of()
                : notificationMessageRequest.getRecipientUserIds().stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        if (recipientUserIds.isEmpty()) {
            throw new IllegalArgumentException("List user is empty");
        }
        NotificationEvent event = this.storeEventAndRecipients(notificationMessageRequest, recipientUserIds);
        List<DeviceToken> tokens = tokenRepo.findByUserIdInAndActiveTrue(recipientUserIds);
        if (tokens.isEmpty()) {
            return;
        }
        pushToRecipients(event.getEventId(), notificationMessageRequest, recipientUserIds);
    }

    @Transactional
    public NotificationEvent storeEventAndRecipients(
            NotificationMessageRequest request,
            List<Integer> recipients) {
        User actor = iUserService.getCurrentUser();

        NotificationEvent event = new NotificationEvent();
        event.setTitle(request.getTitle());
        event.setBody(request.getBody());
        event.setNotificationImage(request.getNotificationImage());
        event.setCreatedAt(LocalDateTime.now());
        event.setNotificationType(request.getNotificationType());
        event.setUser(actor);
        event.setPayload(request.getPayload() == null ? Map.of() : request.getPayload());

        event = eventRepo.save(event);
        if (recipients.size() == 1) {
            NotificationRecipient r = new NotificationRecipient();
            Optional<User> optional = userRepo.findById(recipients.get(0));
            if (optional.isEmpty()) {
                return event;
            }
            User user = optional.get();
            r.setUser(user);
            r.setNotificationEvent(event);
            r.setCreatedAt(LocalDateTime.now());
            r.setReadAt(null);
            r.setEventStatus(EventStatus.PENDING);
            recipientRepo.save(r);
            return event;

        } else {

            List<NotificationRecipient> rows = new ArrayList<>(recipients.size());
            for (Integer rid : recipients) {
                NotificationRecipient r = new NotificationRecipient();

                Optional<User> optional = userRepo.findById(rid);
                if (optional.isEmpty()) {
                    return event;
                }
                User user = optional.get();
                r.setUser(user);
                r.setNotificationEvent(event);
                r.setCreatedAt(LocalDateTime.now());
                r.setReadAt(null);
                r.setEventStatus(EventStatus.PENDING);
                rows.add(r);
            }
            recipientRepo.saveAll(rows);
        }
        return event;
    }

    public void pushToRecipients(Long eventId, NotificationMessageRequest req, List<Integer> recipients) {
        // lấy tokens
        List<String> tokens = tokenRepo.findByUserIdInAndActiveTrue(recipients).stream()
                .map(DeviceToken::getToken)
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .toList();
        if (tokens.isEmpty())
            return;

        Map<String, String> payload = req.getPayload() == null ? new java.util.HashMap<>()
                : new java.util.HashMap<>(req.getPayload());
        payload.putIfAbsent("eventId", String.valueOf(eventId)); // để client mở đúng notification details/deeplink

        Notification notification = Notification.builder()
                .setTitle(req.getTitle())
                .setBody(req.getBody())
                .build();

        for (var batch : chunk(tokens, 500)) {
            MulticastMessage msg = MulticastMessage.builder()
                    .setNotification(notification)
                    .putAllData(payload)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .addAllTokens(batch)
                    .build();

            BatchResponse res;
            try {
                res = messaging.sendEachForMulticast(msg);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException("FCM send failed", e);
            }

            // gom token fail để bulk disable (tránh N+1 query)
            List<String> badTokens = new ArrayList<>();
            for (int i = 0; i < res.getResponses().size(); i++) {
                var r = res.getResponses().get(i);
                if (!r.isSuccessful()) {

                    badTokens.add(batch.get(i));
                }
            }
            // if (!badTokens.isEmpty()) {
            // tokenRepo.deactivateByTokens(badTokens, LocalDateTime.now());
            // }
        }
    }

    private static <T> List<List<T>> chunk(List<T> xs, int size) {
        List<List<T>> out = new ArrayList<>();
        for (int i = 0; i < xs.size(); i += size) {
            out.add(xs.subList(i, Math.min(i + size, xs.size())));
        }
        return out;
    }

    public List<UserNotificationResponse> getAllNotifications(Integer userId) {
        if (userId != null) {
            return recipientRepo.findAllByRecipientUserId(userId);
        } else
            throw new IllegalArgumentException("User not found!");
    }

}
