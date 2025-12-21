package com.example.splitly.domain.entity;

import com.example.splitly.domain.enumerator.PaymentImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_image")
public class PaymentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_public_id", nullable = false)
    private String imagePublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private PaymentImageType imageType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}