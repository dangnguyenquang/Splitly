package com.example.splitly.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdateStatusSuccessPaymentResponse implements Serializable {
    private Integer paymentId;

    private boolean successAccepted;

    private String updatedAt;
}
