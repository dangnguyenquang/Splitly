package com.example.splitly.application.serviceInterface;

import com.example.splitly.domain.entity.ConsensusPayment;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.request.UpdateStatusProcessPaymentRequest;
import com.example.splitly.presentation.dto.response.ConsensusPaymentResponse;

import java.util.List;
import java.util.Set;

public interface IConsensusService {
    public ConsensusPaymentResponse create(ConsensusPaymentRequest consensusPaymentRequest, Payment payment);

//    public ConsensusPaymentResponse updateStatusOfConsensus(Integer consensusPaymentId, UpdateStatusProcessPaymentRequest updateStatusProcessPaymentRequest);

    public Set<ConsensusPaymentResponse> getByPaymentId(Integer paymentId);

    public Set<ConsensusPaymentResponse> createAll(Set<ConsensusPaymentRequest> consensusPaymentRequests, Payment payment);

    public Set<ConsensusPaymentResponse> updateConsensusPaymentById(Payment payment, Set<ConsensusPaymentRequest> consensusPaymentRequests);

    public Set<ConsensusPaymentResponse> getAllConsensusByUser();
}
