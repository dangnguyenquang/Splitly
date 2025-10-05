package com.example.splitly.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ConsensusPaymentRequest implements Serializable {
     private int userId;

     private boolean processAccepted;

     private boolean successAccepted;

     private int paymentId;
}
