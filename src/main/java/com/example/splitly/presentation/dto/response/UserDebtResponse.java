package com.example.splitly.presentation.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDebtResponse implements Serializable {

    private Long userDebtId;

    private UserResponse debtor;
    private UserResponse creditor;

    private Double amount;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime paymentReminderAt;
    private LocalDateTime paymentVerificationReminderAt;
    private Boolean status;
}

