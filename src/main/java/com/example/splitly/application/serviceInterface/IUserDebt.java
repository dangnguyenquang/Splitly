package com.example.splitly.application.serviceInterface;

import java.nio.file.AccessDeniedException;
import java.util.List;


import com.example.splitly.presentation.dto.response.UserDebtResponse;

public interface IUserDebt {
    public List<UserDebtResponse> getAllUserDebt();

    List<UserDebtResponse> getAllDebtsToReceive();

    List<UserDebtResponse> getAllUserDebtInGroup(Long groupId);

    List<UserDebtResponse> getAllUserDebtByPaymentId(int paymentId);

    UserDebtResponse handleDebtClearance(Integer userDebtId) throws AccessDeniedException;
}
