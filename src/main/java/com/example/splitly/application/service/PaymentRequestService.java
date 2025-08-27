package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.mapper.PaymentRequestMapper;
import com.example.splitly.application.mapper.TagMapper;
import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.application.serviceInterface.ITagService;
import com.example.splitly.domain.entity.*;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.ConsensusRepository;
import com.example.splitly.domain.repository.PaymentRequestRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.*;
import com.example.splitly.presentation.dto.response.*;
import com.example.splitly.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestService implements IPaymentRequestService {
    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRequestMapper paymentRequestMapper;
    private final IItemService itemService;
    private final ITagService tagService;
    private final IConsensusService consensusService;
    private final TagMapper tagMapper;
    private final PaymentAssembler paymentAssembler;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ConsensusRepository consensusRepository;

    @Override
    public PaymentResponse create(PaymentRequest paymentRequest) {
        var payment = paymentRequestMapper.toPayment(paymentRequest);

        payment.setStatus(PaymentRequestStatus.WAITING);

        if (paymentRequest.getTag() != null) {
            var tagResponse = tagService.findTagById(paymentRequest.getTag().getTagId());
            payment.setTag(tagMapper.toTag(paymentRequest.getTag()));
        }

        User user = userService.getCurrentUser();
        payment.setUser(user);

        Payment savedPayment = paymentRequestRepository.save(payment);

        Set<ItemResponse> itemResponses = itemService.createAll(paymentRequest.getItems(), savedPayment);

        Set<ConsensusPaymentRequest> consensusPaymentRequests = paymentRequest.getConsensusPayments().stream()
                .peek(consensusPaymentRequest -> {
                            consensusPaymentRequest.setProcessAccepted(false);
                            consensusPaymentRequest.setSuccessAccepted(false);
                        }
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
    public UpdateStatusProcessPaymentResponse updateProcessStatusOfConsensus(Integer paymentId, UpdateStatusProcessPaymentRequest updateStatusProcessPaymentRequest) {
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.WAITING);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        User user = userService.getCurrentUser();
        ConsensusPaymentId id = new ConsensusPaymentId(paymentId, user.getUserId());

        ConsensusPayment consensusPayment = consensusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consensus payment not found"));


        if (consensusPayment.isProcessAccepted() == updateStatusProcessPaymentRequest.isProcessAccepted()) {
            throw new IllegalArgumentException("Consensus payment process status have already " + consensusPayment.isProcessAccepted());
        }

        consensusPayment.setProcessAccepted(updateStatusProcessPaymentRequest.isProcessAccepted());

        consensusPayment.setUpdatedAt(LocalDateTime.now());

        ConsensusPayment saved = consensusRepository.save(consensusPayment);

        UpdateStatusProcessPaymentResponse updateStatusProcessPaymentResponse = new UpdateStatusProcessPaymentResponse();

        updateStatusProcessPaymentResponse.setPaymentId(paymentId);
        updateStatusProcessPaymentResponse.setUpdatedAt(LocalDateTime.now().toString());
        updateStatusProcessPaymentResponse.setProcessAccepted(updateStatusProcessPaymentRequest.isProcessAccepted());

        return updateStatusProcessPaymentResponse;
    }

    @Override
    public UpdateStatusSuccessPaymentResponse updateSuccessStatusOfConsensus(Integer paymentId, UpdateStatusSuccessPaymentRequest updateStatusSuccessPaymentRequest) {
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.SUCCESS);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        User user = userService.getCurrentUser();
        ConsensusPaymentId id = new ConsensusPaymentId(paymentId, user.getUserId());

        ConsensusPayment consensusPayment = consensusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consensus payment not found"));


        if (consensusPayment.isSuccessAccepted() == updateStatusSuccessPaymentRequest.isSuccessAccepted()) {
            throw new IllegalArgumentException("Consensus payment process status have already " + consensusPayment.isSuccessAccepted() );
        }

        consensusPayment.setSuccessAccepted(updateStatusSuccessPaymentRequest.isSuccessAccepted());

        consensusPayment.setUpdatedAt(LocalDateTime.now());

        ConsensusPayment saved = consensusRepository.save(consensusPayment);

        UpdateStatusSuccessPaymentResponse updateStatusSuccessPaymentResponse = new UpdateStatusSuccessPaymentResponse();

        updateStatusSuccessPaymentResponse.setPaymentId(paymentId);
        updateStatusSuccessPaymentResponse.setUpdatedAt(LocalDateTime.now().toString());
        updateStatusSuccessPaymentResponse.setSuccessAccepted(updateStatusSuccessPaymentResponse.isSuccessAccepted());

        return updateStatusSuccessPaymentResponse;
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

        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.WAITING);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, paymentRequest, "Payment request");

        paymentRequestMapper.updatePayment(existingPayment, paymentRequest);

        User user = userService.getCurrentUser();
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

        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.WAITING, PaymentRequestStatus.PROCESSING, PaymentRequestStatus.AWAITING_CONFIRMATION);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

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

        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.WAITING);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

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
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.PROCESSING);
        Payment existingPayment = validatePaymentRequest(
                paymentId,
                allowedStatuses,
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
    public Payment validatePaymentRequest(Integer paymentId, Set<PaymentRequestStatus> expectedStatuses, Object payload, String payloadName) {
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID must not be null");
        }

        if (payload == null && payloadName != null) {
            throw new IllegalArgumentException(payloadName + " must not be null");
        }

        Payment payment = paymentRequestRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment request not found"));

        if (!expectedStatuses.contains(payment.getStatus())) {
            throw new IllegalStateException("Cannot update payment request with status: " + payment.getStatus());
        }

        return payment;
    }

    @Override
    public PaymentResponse changeStatusPaymentRequestToSplit(Integer paymentId) {
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.AWAITING_CONFIRMATION);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        Set<ConsensusPaymentResponse> consensusPaymentResponses = consensusService.getByPaymentId(paymentId);

        consensusPaymentResponses.forEach(res -> {
            if (!res.isSuccessAccepted()) {
                throw new IllegalArgumentException("Everyone didn't confirm this payment yet");
            }
        });

        existingPayment.setStatus(PaymentRequestStatus.READY_TO_SPLIT);

        // DEBT LOGIC HERE

        paymentRequestRepository.save(existingPayment);

        return null;
    }
}
