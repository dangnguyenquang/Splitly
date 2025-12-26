package com.example.splitly.application.facade.notification;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.entity.UserDebt;
import com.example.splitly.domain.enumerator.NotificationType;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NotificationTemplate {

    private NotificationTemplate() {
    }

    // ============= PAYMENT TEMPLATES =============

    /**
     * Notify when a new payment request is created
     */
    public static NotificationMessageRequest paymentRequestCreated(
            Payment payment,
            List<User> consensusUsers
    ) {
        List<Integer> recipientIds = consensusUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        return NotificationMessageRequest.builder()
                .title("New payment request")
                .body(String.format(
                        "%s created a new payment request in %s",
                        payment.getUser().getFullName() != null
                                ? payment.getUser().getFullName()
                                : payment.getUser().getEmail(),
                        payment.getGroupInfo().getGroupName()
                ))
                .notificationType(NotificationType.PAYMENT_REQUEST_CREATED)
                .recipientUserIds(recipientIds)
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "groupId", payment.getGroupInfo().getGroupId().toString(),
                        "groupName", payment.getGroupInfo().getGroupName(),
                        "creatorName", payment.getUser().getFullName() != null
                                ? payment.getUser().getFullName()
                                : payment.getUser().getEmail(),
                        "action", "view_payment_request"
                ))
                .build();
    }

    /**
     * Notify when a payment request is updated
     */
    public static NotificationMessageRequest paymentRequestUpdated(
            Payment payment,
            List<User> consensusUsers
    ) {
        List<Integer> recipientIds = consensusUsers.stream()
                .map(User::getUserId)
                .filter(id -> !id.equals(payment.getUser().getUserId())) // Exclude creator
                .collect(Collectors.toList());

        return NotificationMessageRequest.builder()
                .title("Payment request updated")
                .body(String.format(
                        "%s updated the payment request in %s",
                        payment.getUser().getFullName() != null
                                ? payment.getUser().getFullName()
                                : payment.getUser().getEmail(),
                        payment.getGroupInfo().getGroupName()
                ))
                .notificationType(NotificationType.PAYMENT_REQUEST_UPDATED)
                .recipientUserIds(recipientIds)
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "groupId", payment.getGroupInfo().getGroupId().toString(),
                        "action", "view_payment_request"
                ))
                .build();
    }

    /**
     * Notify when consensus is required (payment created/updated)
     */
    public static NotificationMessageRequest consensusRequired(
            Payment payment,
            User recipient
    ) {
        return NotificationMessageRequest.builder()
                .title("Action required: Confirm payment")
                .body(String.format(
                        "Please confirm the payment request from %s (%.0f VND)",
                        payment.getUser().getFullName() != null
                                ? payment.getUser().getFullName()
                                : payment.getUser().getEmail(),
                        payment.getAmount()
                ))
                .notificationType(NotificationType.PAYMENT_CONSENSUS_REQUIRED)
                .recipientUserIds(List.of(recipient.getUserId()))
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "amount", String.valueOf(payment.getAmount()),
                        "action", "confirm_payment"
                ))
                .build();
    }

    /**
     * Notify payment creator when someone accepts
     */
    public static NotificationMessageRequest consensusAccepted(
            Payment payment,
            User accepter
    ) {
        return NotificationMessageRequest.builder()
                .title("Payment confirmed")
                .body(String.format(
                        "%s confirmed your payment request",
                        accepter.getFullName() != null
                                ? accepter.getFullName()
                                : accepter.getEmail()
                ))
                .notificationType(NotificationType.PAYMENT_CONSENSUS_ACCEPTED)
                .recipientUserIds(List.of(payment.getUser().getUserId()))
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "action", "view_payment_status"
                ))
                .build();
    }

    /**
     * Notify when payment is ready to split
     */
    public static NotificationMessageRequest paymentReadyToSplit(
            Payment payment,
            List<User> allUsers
    ) {
        List<Integer> recipientIds = allUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        return NotificationMessageRequest.builder()
                .title("Payment ready to split")
                .body(String.format(
                        "Payment request in %s is confirmed by everyone. Total: %.0f VND",
                        payment.getGroupInfo().getGroupName(),
                        payment.getAmount()
                ))
                .notificationType(NotificationType.PAYMENT_READY_TO_SPLIT)
                .recipientUserIds(recipientIds)
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "amount", String.valueOf(payment.getAmount()),
                        "groupId", payment.getGroupInfo().getGroupId().toString(),
                        "action", "view_payment"
                ))
                .build();
    }

    /**
     * Notify when payment split is successful
     */
    public static NotificationMessageRequest paymentSplitSuccess(
            Payment payment,
            List<User> allUsers
    ) {
        List<Integer> recipientIds = allUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        return NotificationMessageRequest.builder()
                .title("Payment split successful")
                .body(String.format(
                        "Payment in %s has been split. Check your debts.",
                        payment.getGroupInfo().getGroupName()
                ))
                .notificationType(NotificationType.PAYMENT_SPLIT_SUCCESS)
                .recipientUserIds(recipientIds)
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "groupId", payment.getGroupInfo().getGroupId().toString(),
                        "action", "view_debts"
                ))
                .build();
    }

    /**
     * Notify when payment fails
     */
    public static NotificationMessageRequest paymentFailed(
            Payment payment,
            List<User> consensusUsers
    ) {
        List<Integer> recipientIds = consensusUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        return NotificationMessageRequest.builder()
                .title("Payment request cancelled")
                .body(String.format(
                        "Payment request in %s has been cancelled by the creator",
                        payment.getGroupInfo().getGroupName()
                ))
                .notificationType(NotificationType.PAYMENT_FAILED)
                .recipientUserIds(recipientIds)
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "action", "dismiss"
                ))
                .build();
    }

    /**
     * Notify when a debt is created
     */
    public static NotificationMessageRequest debtCreated(
            UserDebt debt,
            Payment payment
    ) {
        double amount = debt.getAmount();

        return NotificationMessageRequest.builder()
                .title("New debt created")
                .body(String.format(
                        "You owe %.0f VND to %s for payment in %s",
                        amount,
                        debt.getCreditor().getFullName() != null
                                ? debt.getCreditor().getFullName()
                                : debt.getCreditor().getEmail(),
                        payment.getGroupInfo().getGroupName()
                ))
                .notificationType(NotificationType.PAYMENT_DEBT_CREATED)
                .recipientUserIds(List.of(debt.getDebtor().getUserId()))
                .payload(Map.of(
                        "paymentId", payment.getPaymentId().toString(),
                        "amount", String.valueOf(amount),
                        "creditorName", debt.getCreditor().getFullName() != null
                                ? debt.getCreditor().getFullName()
                                : debt.getCreditor().getEmail(),
                        "groupId", payment.getGroupInfo().getGroupId().toString(),
                        "action", "view_debt_details"
                ))
                .build();
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

    // ============= GROUP TEMPLATES =============

    public static NotificationMessageRequest groupInvite(
            GroupInfo group, User inviter, User invitee
    ) {
        return NotificationMessageRequest.builder()
                .title("Group invitation")
                .body(inviter.getUserId() + " invited you to join " + group.getGroupName())
                .notificationType(NotificationType.GROUP_INVITATION)
                .recipientUserIds(List.of(invitee.getUserId()))
                .payload(Map.of())
                .build();
    }

    // ============= CONNECTION TEMPLATES =============

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
