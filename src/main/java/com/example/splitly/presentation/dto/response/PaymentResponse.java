package com.example.splitly.presentation.dto.response;

import com.example.splitly.domain.entity.Tag;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.request.ItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class PaymentResponse implements Serializable {
    private int paymentId;

    private String title;

    private PaymentUserResponse user;

    private TagResponse tag;

    private Set<ItemResponse> items;

    private Set<ConsensusPaymentResponse> consensusPayments;

    private double estimatedAmount;

    private PaymentRequestStatus status;

    private Set<PaymentImageResponse> billImages;

    private Set<PaymentImageResponse> productImages;

    private String paymentRequestNote;

    private double amount;

    private double usedFundAmount;

    private GroupInfoResponse groupInfoResponse;
}