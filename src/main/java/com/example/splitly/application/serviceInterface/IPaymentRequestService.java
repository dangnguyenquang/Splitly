package com.example.splitly.application.serviceInterface;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import io.micrometer.common.lang.Nullable;

import java.util.Set;

public interface IPaymentRequestService {
    public PaymentResponse create(PaymentRequest paymentRequest);

    public PaymentResponse getById(Integer paymentId);

    public Payment getPaymentEntityById(Integer paymentId);

    public Set<PaymentResponse> getAllPaymentRequest();

    public PaymentResponse updatePaymentRequest(PaymentRequest paymentRequest, Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToFailed(Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToProcessing(Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToSplit(Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToAwaitingConfirmation(Integer paymentId, PaymentRequest paymentRequest);

    public Payment validatePaymentRequest(Integer paymentId, PaymentRequestStatus expectedStatus, @Nullable Object payload, @Nullable String payloadName);
}
