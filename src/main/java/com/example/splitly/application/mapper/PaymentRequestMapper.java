package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.PaymentImage;
import com.example.splitly.domain.enumerator.PaymentImageType;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.PaymentImageResponse;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TagMapper.class, UserMapper.class, GroupInfoMapper.class}, imports = {com.example.splitly.domain.enumerator.PaymentImageType.class})

public interface PaymentRequestMapper {
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "groupInfo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "images", ignore = true)
    Payment toPayment(PaymentRequest paymentRequest);

    @Mapping(source = "groupInfo", target = "groupInfoResponse")
    @Mapping(target = "user.userId", source = "user.userId")
    @Mapping(target = "user.fullName", source = "user.fullName")
    @Mapping(target = "billImages",
            expression = "java(filterImages(payment, PaymentImageType.BILL))")
    @Mapping(target = "productImages",
            expression = "java(filterImages(payment, PaymentImageType.PRODUCT))")
    PaymentResponse toPaymentResponse(Payment payment);

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "groupInfo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updatePayment(
            @MappingTarget Payment existingPayment,
            PaymentRequest paymentRequest
    );

    default Set<PaymentImageResponse> filterImages(
            Payment payment,
            PaymentImageType type
    ) {
        if (payment.getImages() == null) return Set.of();

        return payment.getImages()
                .stream()
                .filter(img -> img.getImageType() == type)
                .map(this::toImageResponse)
                .collect(Collectors.toSet());
    }

    PaymentImageResponse toImageResponse(PaymentImage image);
}

