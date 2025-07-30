package com.example.splitly.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsensusPaymentResponse implements Serializable {
    private int userId;

    private int paymentId;

    private LocalDateTime updateAt;

    private LocalDateTime createAt;

    private boolean isAccepted;
}
