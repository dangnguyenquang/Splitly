package com.example.splitly.application.serviceInterface;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.PaymentResponse;

import java.util.Set;

public interface IPaymentRequestService {
    public PaymentResponse create(PaymentRequest paymentRequest);

    public PaymentResponse getById(Integer paymentId);

    public Payment getPaymentEntityById(Integer paymentId);

    public Set<PaymentResponse> getAllPaymentRequest();

    public PaymentRequest updatePaymentRequest(PaymentRequest paymentRequest);

    public void changeStatusPaymentRequestToFailed(Integer paymentId);

    public PaymentRequest changeStatusPaymentRequestToSuccess(PaymentRequest paymentRequest);
}
