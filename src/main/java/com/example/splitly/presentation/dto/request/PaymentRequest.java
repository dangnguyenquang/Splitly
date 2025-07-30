package com.example.splitly.presentation.dto.request;

import com.example.splitly.domain.entity.ConsensusPayment;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.Tag;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PaymentRequest implements Serializable {
    @NotBlank(message = "Tittle can't be blank")
    private String title;

    private TagRequest tag;

    @NotNull(message = "Items cannot be null")
    private Set<ItemRequest> items;

    private Set<ConsensusPaymentRequest> consensusPayments;

    @Min(value = 0, message = "Estimated amount can't lower than 0")
    private double estimatedAmount = 0.0;

    private String imageUrl;

    private String paymentRequestNote;

    @Min(value = 0, message = "Used fund amount can't lower than 0")
    private double usedFundAmount = 0.0;
}
