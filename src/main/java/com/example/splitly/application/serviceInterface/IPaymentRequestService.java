package com.example.splitly.application.serviceInterface;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.request.UpdateStatusProcessPaymentRequest;
import com.example.splitly.presentation.dto.request.UpdateStatusSuccessPaymentRequest;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import com.example.splitly.presentation.dto.response.UpdateStatusProcessPaymentResponse;
import com.example.splitly.presentation.dto.response.UpdateStatusSuccessPaymentResponse;
import io.micrometer.common.lang.Nullable;

import java.util.Set;

public interface IPaymentRequestService {
    public PaymentResponse create(PaymentRequest paymentRequest, Long groupId);

    public PaymentResponse getById(Integer paymentId);

    public Payment getPaymentEntityById(Integer paymentId);

    public Set<PaymentResponse> getAllPaymentRequestByUserId();

    public Set<PaymentResponse> getAllPaymentRequestByConsensusUserId();

    public Set<PaymentResponse> getAllPaymentRequestByGroupId(Integer groupId);

    public UpdateStatusProcessPaymentResponse updateProcessStatusOfConsensus(Integer paymentId, UpdateStatusProcessPaymentRequest updateStatusProcessPaymentRequest);

    public UpdateStatusSuccessPaymentResponse updateSuccessStatusOfConsensus(Integer paymentId, UpdateStatusSuccessPaymentRequest updateStatusSuccessPaymentRequest);

    public PaymentResponse updatePaymentRequest(PaymentRequest paymentRequest, Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToFailed(Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToProcessing(Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToSplit(Integer paymentId);

    public PaymentResponse changeStatusPaymentRequestToAwaitingConfirmation(Integer paymentId, PaymentRequest paymentRequest);

    public Payment validatePaymentRequest(Integer paymentId, Set<PaymentRequestStatus> expectedStatus, @Nullable Object payload, @Nullable String payloadName);
}
