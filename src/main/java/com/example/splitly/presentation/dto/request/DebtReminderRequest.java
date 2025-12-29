package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebtReminderRequest {

    @NotNull(message = "User debt ID is required")
    private Integer userDebtId;

    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;
}