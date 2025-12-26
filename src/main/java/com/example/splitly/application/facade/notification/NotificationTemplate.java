package com.example.splitly.application.facade.notification;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.NotificationType;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;

import java.util.List;
import java.util.Map;

public final class NotificationTemplate {

    private NotificationTemplate() {
    }

    public static NotificationMessageRequest paymentSuccess(Payment p) {
        return NotificationMessageRequest.builder()
                .title("Payment successful")
                .body(String.format(
                        "You paid %,d VND for order #%s",
                        p.getAmount(),
                        p.getPaymentId()
                ))
                .notificationType(NotificationType.PAYMENT)
                .recipientUserIds(List.of(p.getPaymentId()))
                .payload(Map.of(
                        "paymentId", p.getPaymentId().toString(),
                        "orderId", p.getPaymentId().toString()
                ))
                .build();
    }

    public static NotificationMessageRequest groupInvite(
            GroupInfo group, User inviter, User invitee
    ) {
        return NotificationMessageRequest.builder()
                .title("Group invitation")
                .body(inviter.getUserId() + " invited you to join " + group.getGroupName())
                .notificationType(NotificationType.GROUP_INVITATION)
                .recipientUserIds(List.of(invitee.getUserId()))
                .payload(Map.of(
                ))
                .build();
    }

    public static NotificationMessageRequest connectionRequestReceived(
            User requester, User receiver
    ) {
        return NotificationMessageRequest.builder()
                .title("New connection request")
                .body(String.format(
                        "%s (%s) wants to connect with you",
                        requester.getFullName() != null ? requester.getFullName() : requester.getEmail(),
                        requester.getEmail()
                ))
                .notificationType(NotificationType.CONNECTION_REQUEST)
                .recipientUserIds(List.of(receiver.getUserId()))
                .payload(Map.of(
                        "requesterId", String.valueOf(requester.getUserId()),
                        "requesterEmail", requester.getEmail(),
                        "requesterFullName",
                        requester.getFullName() != null ? requester.getFullName() : "",
                        "action", "view_request"
                ))
                .build();
    }

    /**
     * Notification when your connection request is accepted
     */
    public static NotificationMessageRequest connectionRequestAccepted(
            User accepter, User requester
    ) {
        return NotificationMessageRequest.builder()
                .title("Connection request accepted")
                .body(String.format(
                        "%s accepted your connection request. You are now connected!",
                        accepter.getFullName() != null ? accepter.getFullName() : accepter.getEmail()
                ))
                .notificationType(NotificationType.CONNECTION_ACCEPTED)
                .recipientUserIds(List.of(requester.getUserId()))
                .payload(Map.of(
                        "accepterId", String.valueOf(accepter.getUserId()),
                        "accepterEmail", accepter.getEmail(),
                        "accepterFullName",
                        accepter.getFullName() != null ? accepter.getFullName() : "",
                        "action", "view_profile"
                ))
                .build();
    }

    /**
     * Notification when your connection request is rejected
     */
    public static NotificationMessageRequest connectionRequestRejected(
            User rejecter, User requester
    ) {
        return NotificationMessageRequest.builder()
                .title("Connection request declined")
                .body(String.format(
                        "%s declined your connection request",
                        rejecter.getFullName() != null ? rejecter.getFullName() : rejecter.getEmail()
                ))
                .notificationType(NotificationType.CONNECTION_REJECTED)
                .recipientUserIds(List.of(requester.getUserId()))
                .payload(Map.of(
                        "rejecterId", String.valueOf(rejecter.getUserId()),
                        "action", "dismiss"
                ))
                .build();
    }
}

