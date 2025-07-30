package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.ConsensusPayment;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ConsensusPaymentResponse;
import com.example.splitly.presentation.dto.response.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConsensusMapper {
    @Mapping(target = "consensusPaymentId.paymentId", source = "paymentId")
    @Mapping(target = "consensusPaymentId.userId", source = "userId")
    ConsensusPayment toConsensusPayment(ConsensusPaymentRequest consensusPaymentRequest);

    @Mapping(source = "consensusPaymentId.paymentId", target = "paymentId")
    @Mapping(source = "consensusPaymentId.userId", target = "userId")
    ConsensusPaymentResponse toConsensusPaymentResponse(ConsensusPayment consensusPayment);

    @Mapping(target = "consensusPaymentId", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateConsensus(@MappingTarget ConsensusPayment existingConsensus, ConsensusPaymentRequest consensusPaymentRequest);
}
