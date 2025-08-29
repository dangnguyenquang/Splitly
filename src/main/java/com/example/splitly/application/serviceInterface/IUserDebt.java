package com.example.splitly.application.serviceInterface;

import java.util.List;


import com.example.splitly.presentation.dto.response.UserDebtResponse;

public interface IUserDebt {
    public List<UserDebtResponse> getAllUserDebt(Integer debtorId);
    List<UserDebtResponse> getAllUserDebtInGroup(Integer debtorId, Long groupId);
    void handleDebtClearance(Integer creditorId, Integer debtorId);
}
