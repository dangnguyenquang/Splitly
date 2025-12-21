package com.example.splitly.presentation.dto.response;

import com.example.splitly.domain.enumerator.PaymentImageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentImageResponse {
    private Long imageId;
    private String imageUrl;
    private PaymentImageType imageType;
    private LocalDateTime createdAt;
}