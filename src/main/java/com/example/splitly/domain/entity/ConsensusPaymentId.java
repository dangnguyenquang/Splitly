package com.example.splitly.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ConsensusPaymentId implements Serializable {
    private int paymentId;
    private int userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsensusPaymentId that)) return false;
        return paymentId == that.paymentId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, userId);
    }
}
