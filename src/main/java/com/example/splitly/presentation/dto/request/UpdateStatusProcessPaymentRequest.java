package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdateStatusProcessPaymentRequest implements Serializable {
    @NotNull(message = "Process status is required")
    private boolean processAccepted;
}
