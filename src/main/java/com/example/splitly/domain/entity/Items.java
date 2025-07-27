package com.example.splitly.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price_quotation")
    private double priceQuotation;

    @Column(name = "amount")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentRequest paymentRequest;
}
