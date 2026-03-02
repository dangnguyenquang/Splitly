package com.example.splitly.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.splitly.application.serviceInterface.IUserDebt;
import com.example.splitly.presentation.dto.request.DebtReminderRequest;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.presentation.dto.response.UserDebtResponse;
import jakarta.validation.Valid;
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
@Tag(name = "Debt Management", description = "Endpoints for tracking and managing debts between users")
public class DebtController {
    private final IUserDebt userDebt;

    @GetMapping("/pay")
    public ResponseData<?> getAllDebtsToPayByUserId(
            @RequestParam(required = false) Boolean status
    ) {
        List<UserDebtResponse> response = userDebt.getAllUserDebt(status);
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user debt to pay successfully", response);
    }

    @GetMapping("/receive")
    public ResponseData<?> getAllDebtsToReceiveByUserId(
            @RequestParam(required = false) Boolean status
    ) {
        List<UserDebtResponse> response = userDebt.getAllDebtsToReceive(status);
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user debt to receive successfully", response);
    }

    @GetMapping("/{id}/group")
    public ResponseData<?> getAllDebtInGroup(@PathVariable Long id) {
        List<UserDebtResponse> response = userDebt.getAllUserDebtInGroup(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user debt by group successfully", response);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseData<?> confirmDebt(@PathVariable Integer id) throws AccessDeniedException {
        userDebt.handleDebtClearance(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Debt confirmed successfully");
    }

    /**
     * Send payment reminder from creditor to debtor
     * Rate limit: Once every 12 hours
     * <p>
     * POST /debts/remind-payment
     */
    @PostMapping("/remind-payment")
    public ResponseData<?> sendPaymentReminder(@Valid @RequestBody DebtReminderRequest request) {
        userDebt.sendPaymentReminder(request.getUserDebtId(), request.getMessage());

        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Payment reminder sent successfully",
                null
        );
    }

    /**
     * Send verification reminder from debtor to creditor
     * Rate limit: Once every 12 hours
     * <p>
     * POST /debts/remind-verification
     */
    @PostMapping("/remind-verification")
    public ResponseData<?> sendVerificationReminder(@Valid @RequestBody DebtReminderRequest request) {
        userDebt.sendVerificationReminder(request.getUserDebtId(), request.getMessage());

        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Verification reminder sent successfully",
                null
        );
    }
}
