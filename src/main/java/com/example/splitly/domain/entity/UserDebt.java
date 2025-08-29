package com.example.splitly.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_debt")
public class UserDebt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_debt_id")
    private Long userDebtId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "note")
    private String note;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "debtor_id", nullable = false, updatable = false, insertable = false)
    private User debtor;

    @ManyToOne
    @JoinColumn(name = "creditor_id", insertable = false, updatable = false, nullable = false)
    private User creditor;

}
