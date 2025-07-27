package com.example.splitly.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {
    private int itemId;

    private String itemName;

    private double amount;

    private int quantity;

    private double priceQuotation;
}