package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { TagMapper.class, UserMapper.class, GroupInfoMapper.class })
public interface PaymentRequestMapper {
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "tag", ignore = true)
    Payment toPayment(PaymentRequest paymentRequest);

    @Mapping(source = "groupInfo", target = "groupInfoResponse")
    PaymentResponse toPaymentResponse(Payment payment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "tag", ignore = true)
    void updatePayment(@MappingTarget Payment existingPayment, PaymentRequest paymentRequest);
}

