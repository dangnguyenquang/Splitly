package com.example.splitly.application.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.domain.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.splitly.application.mapper.UserDebtMapper;
import com.example.splitly.application.serviceInterface.IUserDebt;
import com.example.splitly.domain.entity.UserDebt;
import com.example.splitly.domain.repository.UserDebtRepository;
import com.example.splitly.presentation.dto.response.UserDebtResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDebtService implements IUserDebt {
    private final UserDebtRepository userDebtRepository;
    private final UserDebtMapper userDebtMapper;
    private final UserService userService;
    private final IGroupUser groupUser;
    private final IPaymentRequestService paymentRequestService;

    @Override
    public List<UserDebtResponse> getAllUserDebt() {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findByDebtorUserId(user.getUserId());
        return userDebtMapper.toUserDebtResponse(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllUserDebtInGroup(Long groupId) {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findAllDebtInGroup(groupId, user.getUserId());
        return userDebtMapper.toUserDebtResponse(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllUserDebtByPaymentId(int paymentId) {
        User user = userService.getCurrentUser();

        List<UserDebt> userDebts = userDebtRepository.findAllDebtByPaymentId(paymentId);

        if (!groupUser.areUsersInGroup(userDebts.getLast().getGroupInfo().getGroupId(), List.of(user.getUserId()))) {
            throw new IllegalStateException("You don't have access to this");
        }

        return userDebtMapper.toUserDebtResponse(userDebts);
    }

    @Override
    public UserDebtResponse handleDebtClearance(Integer userDebtId) throws AccessDeniedException {
        User currentUser = userService.getCurrentUser();

        UserDebt userDebt = userDebtRepository.findByUserDebtId(userDebtId);
        if (userDebt == null) {
            throw new EntityNotFoundException("UserDebt with id " + userDebtId + " not found");
        }

        if (currentUser.getUserId() != userDebt.getDebtor().getUserId()) {
            throw new AccessDeniedException("You are not the debtor of this debt");
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
            UserDebt savedDebt = userDebtRepository.save(userDebt);

            for (UserDebtResponse userDebtResponse : getAllUserDebtByPaymentId(userDebt.getPayment().getPaymentId())) {
                if (!userDebtResponse.getStatus()) break;

                paymentRequestService.changeStatusPaymentRequestToSuccess(userDebt.getPayment().getPaymentId());
            }

            return UserDebtResponse.builder()
                    .amount(savedDebt.getAmount())
                    .note(savedDebt.getNote())
                    .status(savedDebt.getStatus())
                    .createdAt(savedDebt.getCreatedAt())
                    .debtorId(savedDebt.getDebtor().getUserId())
                    .build();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to update debt clearance", ex);
        }
    }
}
