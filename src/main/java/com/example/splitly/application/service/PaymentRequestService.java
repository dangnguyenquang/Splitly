package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.mapper.PaymentRequestMapper;
import com.example.splitly.application.mapper.TagMapper;
import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.application.serviceInterface.ITagService;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.PaymentRequestRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.request.PaymentRequest;
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

        itemService.createAll(paymentRequest.getItems(), savedPayment);

        Set<ConsensusPaymentRequest> consensusPaymentRequests = paymentRequest.getConsensusPayments().stream()
                .peek(consensusPaymentRequest ->
                        consensusPaymentRequest.setAccepted(false)
                )
                .collect(Collectors.toSet());
        consensusService.createAll(consensusPaymentRequests, savedPayment);

        return paymentRequestMapper.toPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getById(Integer paymentId) {
        return paymentRequestMapper.toPaymentResponse(paymentRequestRepository.getByPaymentId(paymentId));
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
        return paymentRequestRepository.findAll().stream().map(paymentRequestMapper::toPaymentResponse).collect(Collectors.toSet());
    }

    @Override
    public PaymentRequest updatePaymentRequest(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public void changeStatusPaymentRequestToFailed(Integer paymentId) {

    }

    @Override
    public PaymentRequest changeStatusPaymentRequestToSuccess(PaymentRequest paymentRequest) {
        return null;
    }
}
