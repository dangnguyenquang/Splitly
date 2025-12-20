package com.example.splitly.domain.entity;

import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_request")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupInfo groupInfo;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @OneToMany(mappedBy = "payment")
    private Set<Items> items;

    @OneToMany(mappedBy = "payment")
    private Set<ConsensusPayment> consensusPayments = new HashSet<>();

    @Column(name = "estimated_amount")
    private double estimatedAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentRequestStatus status;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_public_id")
    private String imagePublicId;

    @Column(name = "payment_request_note")
    private String paymentRequestNote;

    @Column(name = "amount")
    private double amount;

    @Column(name = "used_fund_amount")
    private double usedFundAmount;
}
