package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.PaymentImage;
import com.example.splitly.presentation.dto.request.PaymentImageRequest;
import com.example.splitly.presentation.dto.response.PaymentImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
    imports = {
        com.example.splitly.domain.enumerator.PaymentImageType.class
    }
)
public interface PaymentImageMapper {

    PaymentImage toEntity(PaymentImageRequest request);

    PaymentImageResponse toResponse(PaymentImage image);

    void updateImage(@MappingTarget PaymentImage image, PaymentImageRequest request);
}
