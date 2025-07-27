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
@Table(name = "consensus_payment")
public class ConsensusPayment {
    @EmbeddedId
    private ConsensusPaymentId consensusPaymentId;

    @Column(name = "update_at")
    private int updateAt;

    @Column(name = "create_at")
    private String createAt;

    @Column(name = "is_deleted")
    private String isDeleted;

    @ManyToOne
    @MapsId("paymentId")
    @JoinColumn(name = "payment_id")
    private PaymentRequest paymentRequest;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
