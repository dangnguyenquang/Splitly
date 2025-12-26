package com.example.splitly.application.facade.notification;

import com.example.splitly.application.service.NotificationService;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationFacade {

    private final NotificationService notificationService;

    public void notifyPaymentSuccess(Payment payment) {
        NotificationMessageRequest req =
                NotificationTemplate.paymentSuccess(payment);

        notificationService.sendNotification(req);
    }

    public void notifyGroupInvite(GroupInfo group, User inviter, User invitee) {
        NotificationMessageRequest req =
                NotificationTemplate.groupInvite(group, inviter, invitee);

        notificationService.sendNotification(req);
    }

    /**
     * Notify user when they receive a connection request
     */
    public void notifyConnectionRequestReceived(User requester, User receiver) {
        try {
            NotificationMessageRequest req = NotificationTemplate.connectionRequestReceived(requester, receiver);
            notificationService.sendNotification(req);
            log.info("Sent connection request notification from user {} to user {}",
                    requester.getUserId(), receiver.getUserId());
        } catch (Exception e) {
            log.error("Failed to send connection request notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify user when their connection request is accepted
     */
    public void notifyConnectionRequestAccepted(User accepter, User requester) {
        try {
            NotificationMessageRequest req = NotificationTemplate.connectionRequestAccepted(accepter, requester);
            notificationService.sendNotification(req);
            log.info("Sent connection accepted notification from user {} to user {}",
                    accepter.getUserId(), requester.getUserId());
        } catch (Exception e) {
            log.error("Failed to send connection accepted notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify user when their connection request is rejected
     */
    public void notifyConnectionRequestRejected(User rejecter, User requester) {
        try {
            NotificationMessageRequest req = NotificationTemplate.connectionRequestRejected(rejecter, requester);
            notificationService.sendNotification(req);
            log.info("Sent connection rejected notification from user {} to user {}",
                    rejecter.getUserId(), requester.getUserId());
        } catch (Exception e) {
            log.error("Failed to send connection rejected notification: {}", e.getMessage(), e);
        }
    }
}
