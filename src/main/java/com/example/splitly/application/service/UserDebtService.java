package com.example.splitly.application.service;

import java.util.List;

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
public class UserDebtService implements IUserDebt{
    private final UserDebtRepository userDebtRepository;
    private final UserDebtMapper userDebtMapper;

    @Override
    public List<UserDebtResponse> getAllUserDebt(Integer debtorId) {
        List<UserDebt> userDebts = userDebtRepository.findByDebtorUserId(debtorId);
        return userDebtMapper.toUserDebtResponse(userDebts);
    }

    @Override
    public List<UserDebtResponse> getAllUserDebtInGroup(Integer debtorId, Long groupId) {
        List<UserDebt> userDebts = userDebtRepository.findAllDebtInGroup(groupId, debtorId);
        return userDebtMapper.toUserDebtResponse(userDebts);
    }

    @Override
    public void handleDebtClearance(Integer creditorId, Integer debtor) {
        // check owner
        UserDebt userDebt = userDebtRepository.findByCreditorUserIdAndDebtorUserId(creditorId, debtor);
        if (userDebt != null) {
            userDebt.setStatus(false);
        }
        else {
            throw new EntityNotFoundException("User Debt was not found");
        }
    }
    
}
