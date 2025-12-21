package com.example.splitly.presentation.dto.request;

import com.example.splitly.domain.enumerator.PaymentImageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentImageRequest {
    private String imageUrl;
    private String imagePublicId;
    private PaymentImageType imageType; // BILL | PRODUCT
}
