package com.example.splitly.presentation.dto.request;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ConsensusPaymentRequest implements Serializable {
     private int userId;

     private boolean isAccepted;

     private int paymentId;
}
