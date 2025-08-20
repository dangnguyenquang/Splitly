package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdateStatusSuccessPaymentRequest implements Serializable {
    @NotNull(message = "Success status is required")
    private boolean successAccepted;
}
