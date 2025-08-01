package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.ConsensusPayment;
import com.example.splitly.domain.entity.ConsensusPaymentId;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ConsensusRepository extends JpaRepository<ConsensusPayment, ConsensusPaymentId> {
    Set<ConsensusPayment> findByPaymentPaymentId(Integer paymentId);
}
