package com.example.splitly.presentation.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserDebtResponse implements Serializable {
    private Integer debtorId;

    private String fullName;

    private Double amount;

    private String note;

    private LocalDateTime createdAt;

    private Boolean status;
}
