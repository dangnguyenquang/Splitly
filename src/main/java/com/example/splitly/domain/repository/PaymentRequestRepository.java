package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRequestRepository extends JpaRepository<Payment, Integer> {
    public Payment getByPaymentId(Integer paymentId);
}
