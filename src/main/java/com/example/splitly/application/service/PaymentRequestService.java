package com.example.splitly.application.service;

import com.example.splitly.application.mapper.PaymentRequestMapper;
import com.example.splitly.application.mapper.TagMapper;
import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.application.serviceInterface.ITagService;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.PaymentRequestRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.ConsensusPaymentResponse;
import com.example.splitly.presentation.dto.response.ItemResponse;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import com.example.splitly.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestService implements IPaymentRequestService {
    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRequestMapper paymentRequestMapper;
    private final UserRepository userRepository;
    private final IItemService itemService;
    private final ITagService tagService;
    private final IConsensusService consensusService;
    private final TagMapper tagMapper;
    private final PaymentAssembler paymentAssembler;

    @Override
    public PaymentResponse create(PaymentRequest paymentRequest) {
        var payment = paymentRequestMapper.toPayment(paymentRequest);

        payment.setStatus(PaymentRequestStatus.WAITING);

        if (paymentRequest.getTag() != null) {
            var tagResponse = tagService.findTagById(paymentRequest.getTag().getTagId());
            payment.setTag(tagMapper.toTag(paymentRequest.getTag()));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        payment.setUser(user);

        Payment savedPayment = paymentRequestRepository.save(payment);

        Set<ItemResponse> itemResponses = itemService.createAll(paymentRequest.getItems(), savedPayment);

        Set<ConsensusPaymentRequest> consensusPaymentRequests = paymentRequest.getConsensusPayments().stream()
                .peek(consensusPaymentRequest ->
                        consensusPaymentRequest.setAccepted(false)
                )
                .collect(Collectors.toSet());
        Set<ConsensusPaymentResponse> consensusPaymentResponses = consensusService.createAll(consensusPaymentRequests, savedPayment);

        PaymentResponse paymentResponse = paymentRequestMapper.toPaymentResponse(payment);
        paymentResponse.setItems(itemResponses);
        paymentResponse.setConsensusPayments(consensusPaymentResponses);

        return paymentResponse;
    }

    @Override
    public PaymentResponse getById(Integer paymentId) {
        return paymentAssembler.toPaymentResponse(paymentRequestRepository.getByPaymentId(paymentId));
    }

    @Override
    public Payment getPaymentEntityById(Integer paymentId) {
        Payment payment = paymentRequestRepository.getByPaymentId(paymentId);
        if (payment == null) {
            throw new EntityNotFoundException("Payment not found with id: " + paymentId);
        }
        return payment;
    }

    @Override
    public Set<PaymentResponse> getAllPaymentRequest() {
        return paymentRequestRepository.findAll().stream().map(
                paymentAssembler::toPaymentResponse
        ).collect(Collectors.toSet());
    }

    @Override
    public PaymentResponse updatePaymentRequest(PaymentRequest paymentRequest, Integer paymentId) {
        if (paymentRequest == null || paymentId == null) {
            throw new IllegalArgumentException("Payment request or ID must not be null");
        }

        Payment existingPayment = paymentRequestRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));

        if (existingPayment.getStatus() != PaymentRequestStatus.WAITING) {
            throw new IllegalStateException("Cannot update payment request with status: " + existingPayment.getStatus());
        }

        paymentRequestMapper.updatePayment(existingPayment, paymentRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingPayment.setUser(user);

        Set<ItemResponse> itemResponses = itemService.updateItemsByPaymentId(existingPayment, paymentRequest.getItems());
        Set<ConsensusPaymentResponse> consensusPaymentResponses = consensusService.updateConsensusPaymentById(existingPayment, paymentRequest.getConsensusPayments());

        Payment savedPayment = paymentRequestRepository.save(existingPayment);

        PaymentResponse paymentResponse = paymentRequestMapper.toPaymentResponse(savedPayment);
        paymentResponse.setItems(itemResponses);
        paymentResponse.setConsensusPayments(consensusPaymentResponses);

        return paymentResponse;
    }

    @Override
    public void changeStatusPaymentRequestToFailed(Integer paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID must not be null");
        }

        Payment existingPayment = paymentRequestRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));

        if (existingPayment.getStatus() != PaymentRequestStatus.WAITING) {
            throw new IllegalStateException("Cannot update payment request with status: " + existingPayment.getStatus());
        }

        existingPayment.setStatus(PaymentRequestStatus.FAILED);

        paymentRequestRepository.save(existingPayment);
    }

    @Override
    public PaymentResponse changeStatusPaymentRequestToSuccess(PaymentRequest paymentRequest, Integer paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID must not be null");
        }

        Payment existingPayment = paymentRequestRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));

        if (existingPayment.getStatus() != PaymentRequestStatus.WAITING) {
            throw new IllegalStateException("Cannot update payment request with status: " + existingPayment.getStatus());
        }

        existingPayment.setStatus(PaymentRequestStatus.SUCCESS);

        // DEBT LOGIC HERE

        paymentRequestRepository.save(existingPayment);

        return null;
    }
}
