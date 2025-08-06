package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.mapper.PaymentRequestMapper;
import com.example.splitly.application.mapper.TagMapper;
import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.application.serviceInterface.ITagService;
import com.example.splitly.domain.entity.ConsensusPayment;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.PaymentRequestRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.ConsensusPaymentResponse;
import com.example.splitly.presentation.dto.response.ItemResponse;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import com.example.splitly.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
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
    private final ItemMapper itemMapper;

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
//        if (paymentRequest == null || paymentId == null) {
//            throw new IllegalArgumentException("Payment request or ID must not be null");
//        }
//
//        Payment existingPayment = paymentRequestRepository.findById(paymentId)
//                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));
//
//        if (existingPayment.getStatus() != PaymentRequestStatus.WAITING) {
//            throw new IllegalStateException("Cannot update payment request with status: " + existingPayment.getStatus());
//        }

        Payment existingPayment = validatePaymentRequest(paymentId, PaymentRequestStatus.WAITING, paymentRequest, "Payment request");

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
    public PaymentResponse changeStatusPaymentRequestToFailed(Integer paymentId) {
//        if (paymentId == null) {
//            throw new IllegalArgumentException("Payment ID must not be null");
//        }
//
//        Payment existingPayment = paymentRequestRepository.findById(paymentId)
//                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));
//
//        if (existingPayment.getStatus() != PaymentRequestStatus.WAITING) {
//            throw new IllegalStateException("Cannot update payment request with status: " + existingPayment.getStatus());
//        }

        Payment existingPayment = validatePaymentRequest(paymentId, PaymentRequestStatus.WAITING, null, null);

        existingPayment.setStatus(PaymentRequestStatus.FAILED);

        return paymentRequestMapper.toPaymentResponse(paymentRequestRepository.save(existingPayment));
    }

    @Override
    public PaymentResponse changeStatusPaymentRequestToProcessing(Integer paymentId) {
//        if (paymentId == null) {
//            throw new IllegalArgumentException("Payment ID must not be null");
//        }
//
//        Payment existingPayment = paymentRequestRepository.findById(paymentId)
//                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));
//
//        if (existingPayment.getStatus() != PaymentRequestStatus.WAITING) {
//            throw new IllegalStateException("Cannot update payment request with status: " + existingPayment.getStatus());
//        }

        Payment existingPayment = validatePaymentRequest(paymentId, PaymentRequestStatus.WAITING, null, null);

        Set<ConsensusPaymentResponse> consensusPaymentResponses = consensusService.getByPaymentId(paymentId);

        consensusPaymentResponses.forEach(res -> {
            if (!res.isProcessAccepted()) {
                throw new IllegalArgumentException("Everyone didn't accept this payment yet");
            }
        });

        existingPayment.setStatus(PaymentRequestStatus.PROCESSING);

        return paymentRequestMapper.toPaymentResponse(paymentRequestRepository.save(existingPayment));
    }


    @Override
    public PaymentResponse changeStatusPaymentRequestToAwaitingConfirmation(Integer paymentId, PaymentRequest paymentRequest) {
        Payment existingPayment = validatePaymentRequest(
                paymentId,
                PaymentRequestStatus.PROCESSING,
                paymentRequest,
                "Payment request"
        );

        if (paymentRequest.getItems() == null || paymentRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Payment request must contain at least one item.");
        }

        Set<Items> validatedItems = new HashSet<>();

        for (ItemRequest itemRequest : paymentRequest.getItems()) {
            if (itemRequest.getPriceQuotation() < 0) {
                throw new IllegalArgumentException("Item '" + itemRequest.getItemName() + "' has invalid price quotation.");
            }

            if (itemRequest.getQuantity() < 1) {
                throw new IllegalArgumentException("Item '" + itemRequest.getItemName() + "' has invalid quantity.");
            }

            double amount = itemRequest.getPriceQuotation() * itemRequest.getQuantity();

            Items item = itemMapper.toItems(itemRequest);
            item.setAmount(amount);
            item.setPayment(existingPayment);

            validatedItems.add(item);
        }

        existingPayment.setItems(validatedItems);

        existingPayment.setStatus(PaymentRequestStatus.AWAITING_CONFIRMATION);

        paymentRequestRepository.save(existingPayment);
        Set<ItemResponse> itemResponses = itemService.updateItemsByPaymentId(existingPayment, paymentRequest.getItems());


        return paymentRequestMapper.toPaymentResponse(existingPayment);
    }

    @Override
    public Payment validatePaymentRequest(Integer paymentId, PaymentRequestStatus expectedStatus, Object payload, String payloadName) {
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID must not be null");
        }

        if (payload != null && payloadName != null) {
            throw new IllegalArgumentException(payloadName + " must not be null");
        }

        Payment payment = paymentRequestRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));

        if (payment.getStatus() != expectedStatus) {
            throw new IllegalStateException("Cannot update payment request with status: " + payment.getStatus());
        }

        return payment;
    }

    @Override
    public PaymentResponse changeStatusPaymentRequestToSplit(Integer paymentId) {
        Payment existingPayment = validatePaymentRequest(paymentId, PaymentRequestStatus.PROCESSING, null, null);

        existingPayment.setStatus(PaymentRequestStatus.READY_TO_SPLIT);

        // DEBT LOGIC HERE

        paymentRequestRepository.save(existingPayment);

        return null;
    }
}
