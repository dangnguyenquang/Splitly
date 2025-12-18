package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IUserDebt;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.presentation.dto.response.UserDebtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/debt")
@RequiredArgsConstructor
@Validated
public class DebtController {
    private final IUserDebt userDebt;

    @GetMapping("/pay")
    public ResponseData<?> getAllDebtsToPayByUserId() {
        List<UserDebtResponse> response = userDebt.getAllUserDebt();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user debt to pay successfully", response);
    }

    @GetMapping("/receive")
    public ResponseData<?> getAllDebtsToReceiveByUserId() {
        List<UserDebtResponse> response = userDebt.getAllDebtsToReceive();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user debt to receive successfully", response);
    }

    @GetMapping("/{id}/group")
    public ResponseData<?> getAllDebtInGroup(@PathVariable Long id) {
        List<UserDebtResponse> response = userDebt.getAllUserDebtInGroup(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user debt by group successfully", response);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseData<?> confirmDebt(@PathVariable Integer id) throws AccessDeniedException {
        UserDebtResponse response = userDebt.handleDebtClearance(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Debt confirmed successfully", response);

    }
}
