package com.example.splitly.presentation.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDebtResponse {
    private Integer debtorId;

    private String fullName;

    private Double amount;

    private String note;

    private LocalDateTime createdAt;

    private Boolean status;
}
