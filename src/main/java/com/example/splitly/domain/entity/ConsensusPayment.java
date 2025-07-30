package com.example.splitly.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consensus_payment")
public class ConsensusPayment {
    @EmbeddedId
    private ConsensusPaymentId consensusPaymentId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_accepted")
    private boolean isAccepted;

    @ManyToOne
    @MapsId("paymentId")
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
