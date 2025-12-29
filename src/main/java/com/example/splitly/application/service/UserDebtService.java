package com.example.splitly.application.service;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.example.splitly.application.facade.notification.NotificationFacade;
import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.splitly.application.mapper.UserDebtMapper;
import com.example.splitly.application.serviceInterface.IUserDebt;
import com.example.splitly.domain.entity.UserDebt;
import com.example.splitly.domain.repository.UserDebtRepository;
import com.example.splitly.presentation.dto.response.UserDebtResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDebtService implements IUserDebt {
    private final UserDebtRepository userDebtRepository;
    private final UserDebtMapper userDebtMapper;
    private final UserService userService;
    private final IGroupUser groupUser;
    private final IPaymentRequestService paymentRequestService;
    private final NotificationFacade notificationFacade;

    private static final long REMINDER_COOLDOWN_HOURS = 12;

    //    Check currently debt
    @Override
    public List<UserDebtResponse> getAllUserDebt() {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findByDebtorUserId(user.getUserId());
        return userDebtMapper.toResponses(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllDebtsToReceive() {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findByCreditorUserId(user.getUserId());
        return userDebtMapper.toResponses(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllUserDebtInGroup(Long groupId) {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findAllDebtInGroup(groupId, user.getUserId());
        return userDebtMapper.toResponses(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllUserDebtByPaymentId(int paymentId) {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findAllDebtByPaymentId(paymentId);

        if (!groupUser.areUsersInGroup(userDebts.getLast().getGroupInfo().getGroupId(), List.of(user.getUserId()))) {
            throw new IllegalStateException("You don't have access to this");
        }

        return userDebtMapper.toResponses(userDebts);
    }

    @Override
    @Transactional
    public void handleDebtClearance(Integer userDebtId) throws AccessDeniedException {
        User currentUser = userService.getCurrentUser();

        UserDebt userDebt = userDebtRepository.findByUserDebtId(userDebtId);
        if (userDebt == null) {
            throw new EntityNotFoundException("UserDebt with id " + userDebtId + " not found");
        }

        if (currentUser.getUserId() != userDebt.getCreditor().getUserId()) {
            throw new AccessDeniedException("You are not the creditor of this debt");
        }

        if (Boolean.TRUE.equals(userDebt.getStatus())) {
            throw new IllegalStateException("This debt has already been cleared");
        }

        if (userDebt.getAmount() == null || userDebt.getAmount() <= 0) {
            throw new IllegalArgumentException("Debt amount must be greater than zero");
        }

        try {
            userDebt.setStatus(true);
            userDebt.setCreatedAt(LocalDateTime.now());
            userDebtRepository.save(userDebt);
            userDebtRepository.flush();

            boolean allPaid = true;

            for (UserDebtResponse userDebtResponse
                    : getAllUserDebtByPaymentId(userDebt.getPayment().getPaymentId())) {

                if (!Boolean.TRUE.equals(userDebtResponse.getStatus())) {
                    System.out.println(String.valueOf(userDebtResponse));
                    allPaid = false;
                    break;
                }
            }

            if (allPaid) {
                paymentRequestService
                        .changeStatusPaymentRequestToSuccess(
                                userDebt.getPayment().getPaymentId()
                        );
            }

//            return userDebtMapper.toResponse(savedDebt);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to update debt clearance", ex);
        }
    }

    /**
     * Send payment reminder from creditor to debtor
     * Can only be sent every 12 hours
     */
    @Transactional
    public void sendPaymentReminder(Integer userDebtId, String message) {
        User currentUser = userService.getCurrentUser();

        // 1. Validate debt exists
        UserDebt debt = userDebtRepository.findById(userDebtId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Debt not found with id: " + userDebtId));

        // 2. Validate current user is the creditor
        if (!(currentUser.getUserId() == debt.getCreditor().getUserId())) {
            throw new IllegalStateException(
                    "Only the creditor can send payment reminders. " +
                            "You are not the creditor of this debt."
            );
        }

        // 3. Validate debt is not already paid
        if (Boolean.TRUE.equals(debt.getStatus())) {
            throw new IllegalStateException(
                    "Cannot send payment reminder for a debt that is already marked as paid"
            );
        }

        // 4. Check 12-hour cooldown period
        LocalDateTime lastReminderTime = debt.getPaymentReminderAt();
        if (lastReminderTime != null) {
            Duration timeSinceLastReminder = Duration.between(lastReminderTime, LocalDateTime.now());
            long hoursSinceLastReminder = timeSinceLastReminder.toHours();

            if (hoursSinceLastReminder < REMINDER_COOLDOWN_HOURS) {
                long hoursRemaining = REMINDER_COOLDOWN_HOURS - hoursSinceLastReminder;
                throw new IllegalStateException(
                        String.format(
                                "You can only send payment reminders every %d hours. " +
                                        "Please wait %d more hour(s) before sending another reminder.",
                                REMINDER_COOLDOWN_HOURS,
                                hoursRemaining
                        )
                );
            }
        }

        // 5. Validate message length if provided
        if (message != null && message.length() > 500) {
            throw new IllegalArgumentException("Message is too long. Maximum 500 characters allowed.");
        }

        // 6. Update reminder timestamp
        debt.setPaymentReminderAt(LocalDateTime.now());
        userDebtRepository.save(debt);

        // 7. Send notification
        notificationFacade.notifyPaymentReminder(debt, message);
    }

    /**
     * Send verification reminder from debtor to creditor
     * Can only be sent every 12 hours
     */
    @Transactional
    public void sendVerificationReminder(Integer userDebtId, String message) {
        User currentUser = userService.getCurrentUser();

        // 1. Validate debt exists
        UserDebt debt = userDebtRepository.findById(userDebtId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Debt not found with id: " + userDebtId));

        // 2. Validate current user is the debtor
        if (!(currentUser.getUserId() == debt.getDebtor().getUserId())) {
            throw new IllegalStateException(
                    "Only the debtor can send verification reminders. " +
                            "You are not the debtor of this debt."
            );
        }

        // 3. Validate debt is not already verified as paid
        if (Boolean.TRUE.equals(debt.getStatus())) {
            throw new IllegalStateException(
                    "This debt is already marked as paid. No verification reminder needed."
            );
        }

        // 4. Check 12-hour cooldown period
        LocalDateTime lastVerificationTime = debt.getPaymentVerificationReminderAt();
        if (lastVerificationTime != null) {
            Duration timeSinceLastVerification = Duration.between(lastVerificationTime, LocalDateTime.now());
            long hoursSinceLastVerification = timeSinceLastVerification.toHours();

            if (hoursSinceLastVerification < REMINDER_COOLDOWN_HOURS) {
                long hoursRemaining = REMINDER_COOLDOWN_HOURS - hoursSinceLastVerification;
                throw new IllegalStateException(
                        String.format(
                                "You can only send verification reminders every %d hours. " +
                                        "Please wait %d more hour(s) before sending another reminder.",
                                REMINDER_COOLDOWN_HOURS,
                                hoursRemaining
                        )
                );
            }
        }

        // 5. Validate message length if provided
        if (message != null && message.length() > 500) {
            throw new IllegalArgumentException("Message is too long. Maximum 500 characters allowed.");
        }

        // 6. Update verification reminder timestamp
        debt.setPaymentVerificationReminderAt(LocalDateTime.now());
        userDebtRepository.save(debt);

        // 7. Send notification
        notificationFacade.notifyVerificationReminder(debt, message);
    }

    @Override
    public List<UserDebtResponse> getAllUserDebt(Boolean status) {
        User user = userService.getCurrentUser();
        List<UserDebt> userDebts;

        if (status == null) {
            userDebts = userDebtRepository.findByDebtorUserId(user.getUserId());
        } else {
            userDebts = userDebtRepository.findByDebtorUserIdAndStatus((long) user.getUserId(), status);
        }

        return userDebtMapper.toResponses(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllDebtsToReceive(Boolean status) {
        User user = userService.getCurrentUser();
        List<UserDebt> userDebts;

        if (status == null) {
            userDebts = userDebtRepository.findByCreditorUserId(user.getUserId());
        } else {
            userDebts = userDebtRepository.findByCreditorUserIdAndStatus((long) user.getUserId(), status);
        }

        return userDebtMapper.toResponses(userDebts);
    }

    /**
     * Get time remaining until next payment reminder can be sent
     */
    public Long getHoursUntilNextPaymentReminder(Integer userDebtId) {
        UserDebt debt = userDebtRepository.findById(userDebtId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Debt not found"));

        LocalDateTime lastReminderTime = debt.getPaymentReminderAt();
        if (lastReminderTime == null) {
            return 0L; // Can send immediately
        }

        Duration timeSinceLastReminder = Duration.between(lastReminderTime, LocalDateTime.now());
        long hoursSinceLastReminder = timeSinceLastReminder.toHours();

        if (hoursSinceLastReminder >= REMINDER_COOLDOWN_HOURS) {
            return 0L; // Can send now
        }

        return REMINDER_COOLDOWN_HOURS - hoursSinceLastReminder;
    }

    /**
     * Get time remaining until next verification reminder can be sent
     */
    public Long getHoursUntilNextVerificationReminder(Integer userDebtId) {
        UserDebt debt = userDebtRepository.findById(userDebtId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Debt not found"));

        LocalDateTime lastVerificationTime = debt.getPaymentVerificationReminderAt();
        if (lastVerificationTime == null) {
            return 0L; // Can send immediately
        }

        Duration timeSinceLastVerification = Duration.between(lastVerificationTime, LocalDateTime.now());
        long hoursSinceLastVerification = timeSinceLastVerification.toHours();

        if (hoursSinceLastVerification >= REMINDER_COOLDOWN_HOURS) {
            return 0L; // Can send now
        }

        return REMINDER_COOLDOWN_HOURS - hoursSinceLastVerification;
    }
}
