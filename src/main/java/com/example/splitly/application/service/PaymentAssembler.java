package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ConsensusMapper;
import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.mapper.PaymentRequestMapper;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentAssembler {

    private final PaymentRequestMapper paymentMapper;
    private final ItemMapper itemMapper;
    private final ConsensusMapper consensusMapper;

    public PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse response = paymentMapper.toPaymentResponse(payment);

        // Manually set nested collections using existing mappers
        if (payment.getItems() != null) {
            response.setItems(
                    payment.getItems().stream()
                            .map(itemMapper::toItemResponse)
                            .collect(Collectors.toSet()));
        }

        if (payment.getConsensusPayments() != null) {
            response.setConsensusPayments(
                    payment.getConsensusPayments().stream()
                            .map(consensusMapper::toConsensusPaymentResponse)
                            .collect(Collectors.toSet()));
        }

        return response;
    }
}
