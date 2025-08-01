package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ItemRequest implements Serializable {
    private Integer itemId;

    private int paymentId;

    @NotBlank(message = "Item name can't be blank")
    private String itemName;

    @Min(value = 0, message = "Amount must be at least 0")
    private double amount;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    @Min(value = 0, message = "Price quotation must be at least 0")
    private double priceQuotation;
}
