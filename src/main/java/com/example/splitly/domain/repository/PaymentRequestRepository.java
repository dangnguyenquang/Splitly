package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRequestRepository extends JpaRepository<Payment, Integer> {
    public Payment getByPaymentId(Integer paymentId);

    public List<Payment> getByGroupInfo_GroupId(Integer groupId);

    public List<Payment> getByUser_UserId(Integer userId);

    public List<Payment> findDistinctByConsensusPayments_User_UserId(Integer userId);

}
