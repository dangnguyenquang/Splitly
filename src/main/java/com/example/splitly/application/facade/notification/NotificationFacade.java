package com.example.splitly.application.facade.notification;

import com.example.splitly.application.service.NotificationService;
import com.example.splitly.domain.entity.*;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationFacade {

    private final NotificationService notificationService;

    // ============= PAYMENT NOTIFICATIONS =============

    /**
     * Notify when payment request is created
     */
    public void notifyPaymentRequestCreated(Payment payment) {
        try {
            List<User> consensusUsers = payment.getConsensusPayments().stream()
                    .map(ConsensusPayment::getUser)
                    .collect(Collectors.toList());

            NotificationMessageRequest req = NotificationTemplate.paymentRequestCreated(payment, consensusUsers);
            notificationService.sendNotification(req);

            log.info("Sent payment request created notification for payment {} to {} users",
                    payment.getPaymentId(), consensusUsers.size());
        } catch (Exception e) {
            log.error("Failed to send payment request created notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify when payment request is updated
     */
    public void notifyPaymentRequestUpdated(Payment payment) {
        try {
            List<User> consensusUsers = payment.getConsensusPayments().stream()
                    .map(ConsensusPayment::getUser)
                    .collect(Collectors.toList());

            NotificationMessageRequest req = NotificationTemplate.paymentRequestUpdated(payment, consensusUsers);
            notificationService.sendNotification(req);

            log.info("Sent payment request updated notification for payment {}", payment.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to send payment request updated notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify individual user that consensus is required
     */
    public void notifyConsensusRequired(Payment payment, User recipient) {
        try {
            NotificationMessageRequest req = NotificationTemplate.consensusRequired(payment, recipient);
            notificationService.sendNotification(req);

            log.info("Sent consensus required notification to user {} for payment {}",
                    recipient.getUserId(), payment.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to send consensus required notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify payment creator when someone accepts
     */
    public void notifyConsensusAccepted(Payment payment, User accepter) {
        try {
            NotificationMessageRequest req = NotificationTemplate.consensusAccepted(payment, accepter);
            notificationService.sendNotification(req);

            log.info("Sent consensus accepted notification for payment {} from user {}",
                    payment.getPaymentId(), accepter.getUserId());
        } catch (Exception e) {
            log.error("Failed to send consensus accepted notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify when payment is ready to split
     */
    public void notifyPaymentReadyToSplit(Payment payment) {
        try {
            List<User> allUsers = payment.getConsensusPayments().stream()
                    .map(ConsensusPayment::getUser)
                    .collect(Collectors.toList());
            allUsers.add(payment.getUser()); // Add creator

            NotificationMessageRequest req = NotificationTemplate.paymentReadyToSplit(payment, allUsers);
            notificationService.sendNotification(req);

            log.info("Sent payment ready to split notification for payment {}", payment.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to send payment ready to split notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify when payment split is successful
     */
    public void notifyPaymentSplitSuccess(Payment payment) {
        try {
            List<User> allUsers = payment.getConsensusPayments().stream()
                    .map(ConsensusPayment::getUser)
                    .collect(Collectors.toList());
            allUsers.add(payment.getUser());

            NotificationMessageRequest req = NotificationTemplate.paymentSplitSuccess(payment, allUsers);
            notificationService.sendNotification(req);

            log.info("Sent payment split success notification for payment {}", payment.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to send payment split success notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify when payment fails
     */
    public void notifyPaymentFailed(Payment payment) {
        try {
            List<User> consensusUsers = payment.getConsensusPayments().stream()
                    .map(ConsensusPayment::getUser)
                    .collect(Collectors.toList());

            NotificationMessageRequest req = NotificationTemplate.paymentFailed(payment, consensusUsers);
            notificationService.sendNotification(req);

            log.info("Sent payment failed notification for payment {}", payment.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to send payment failed notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify when debt is created
     */
    public void notifyDebtCreated(UserDebt debt, Payment payment) {
        try {
            NotificationMessageRequest req = NotificationTemplate.debtCreated(debt, payment);
            notificationService.sendNotification(req);

            log.info("Sent debt created notification to user {} for debt {}",
                    debt.getDebtor().getUserId(), debt.getDebtor().getUserId());
        } catch (Exception e) {
            log.error("Failed to send debt created notification: {}", e.getMessage(), e);
        }
    }

    // ============= EXISTING NOTIFICATIONS =============

    public void notifyPaymentSuccess(Payment payment) {
        NotificationMessageRequest req = NotificationTemplate.paymentSuccess(payment);
        notificationService.sendNotification(req);
    }

    public void notifyGroupInvite(GroupInfo group, User inviter, User invitee) {
        NotificationMessageRequest req = NotificationTemplate.groupInvite(group, inviter, invitee);
        notificationService.sendNotification(req);
    }

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