package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.PaymentImage;
import com.example.splitly.domain.enumerator.PaymentImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentImageRepository extends JpaRepository<PaymentImage, Long> {

    List<PaymentImage> findByPayment_PaymentId(Integer paymentId);

    List<PaymentImage> findByPayment_PaymentIdAndImageType(
            Integer paymentId,
            PaymentImageType imageType
    );

    long countByPayment_PaymentIdAndImageType(
            Integer paymentId,
            PaymentImageType imageType
    );

    List<PaymentImage> findByPaymentAndImageTypeOrderByImageIdAsc(
            Payment payment,
            PaymentImageType imageType
    );
}
