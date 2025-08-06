package com.example.splitly.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsensusPaymentResponse implements Serializable {
    private int userId;

    private String email;

    private String fullName;

    private String phone;

    private int paymentId;

    private LocalDateTime updateAt;

    private LocalDateTime createAt;

    private boolean isProcessAccepted;

    private boolean isSuccessAccepted;
}
