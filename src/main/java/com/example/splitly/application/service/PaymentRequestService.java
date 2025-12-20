package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.mapper.PaymentRequestMapper;
import com.example.splitly.application.mapper.TagMapper;
import com.example.splitly.application.serviceInterface.*;
import com.example.splitly.domain.entity.*;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.ConsensusRepository;
import com.example.splitly.domain.repository.PaymentRequestRepository;
import com.example.splitly.domain.repository.UserDebtRepository;
import com.example.splitly.presentation.dto.request.*;
import com.example.splitly.presentation.dto.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final UserDebtRepository userDebtRepository;
    private final IGroupUser groupUser;
    private final IGroupInfo groupInfo;
    private final ImageCloudinaryService imageCloudinaryService;

    @Override
    public PaymentResponse create(PaymentRequest paymentRequest, Long groupId) {
        // 1. Get the current user
        User currentUser = userService.getCurrentUser();

        // 2. Validate that all involved users belong to the group
        List<Integer> userIds = Stream.concat(
                paymentRequest.getConsensusPayments().stream()
                        .map(ConsensusPaymentRequest::getUserId),
                Stream.of(currentUser.getUserId())
        ).collect(Collectors.toList());

        if (!groupUser.areUsersInGroup(groupId, userIds)) {
            throw new IllegalArgumentException("Group does not exist or user is not in this group");
        }

        // 3. Map PaymentRequest to Payment entity
        Payment payment = paymentRequestMapper.toPayment(paymentRequest);
        payment.setStatus(PaymentRequestStatus.WAITING);
        payment.setUser(currentUser);
        payment.setGroupInfo(groupInfo.findGroupInfo(groupId));

        // 4. Handle tag if provided
        if (paymentRequest.getTag() != null) {
            tagService.findTagById(paymentRequest.getTag().getTagId()); // validate tag exists
            payment.setTag(tagMapper.toTag(paymentRequest.getTag()));
        }

        // 5. Save Payment entity
        Payment savedPayment = paymentRequestRepository.save(payment);

        // 6. Create items related to the payment
        Set<ItemResponse> itemResponses = itemService.createAll(paymentRequest.getItems(), savedPayment);

        // 7. Prepare consensus payment requests
        Set<ConsensusPaymentRequest> consensusPaymentRequests = paymentRequest.getConsensusPayments().stream()
                .peek(c -> {
                    c.setProcessAccepted(false);
                    c.setSuccessAccepted(false);
                })
                .collect(Collectors.toSet());

        Set<ConsensusPaymentResponse> consensusPaymentResponses =
                consensusService.createAll(consensusPaymentRequests, savedPayment);

        // 8. Map to response DTO
        PaymentResponse paymentResponse = paymentRequestMapper.toPaymentResponse(savedPayment);
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
    public Set<PaymentResponse> getAllPaymentRequestByUserId() {
        User user = userService.getCurrentUser();
        return paymentRequestRepository.getByUser_UserId(user.getUserId()).stream().map(
                paymentAssembler::toPaymentResponse
        ).collect(Collectors.toSet());
    }

    @Override
    public Set<PaymentResponse> getAllPaymentRequestByConsensusUserId() {
        User user = userService.getCurrentUser();
        return paymentRequestRepository.findDistinctByConsensusPayments_User_UserId(user.getUserId()).stream().map(
                paymentAssembler::toPaymentResponse
        ).collect(Collectors.toSet());
    }

    @Override
    public Set<PaymentResponse> getAllPaymentRequestByGroupId(Long groupId) {
        return paymentRequestRepository.getByGroupInfo_GroupId(groupId).stream().map(
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
        updateStatusProcessPaymentResponse.setUpdatedAt(saved.getUpdatedAt().toString());
        updateStatusProcessPaymentResponse.setProcessAccepted(saved.isProcessAccepted());

        return updateStatusProcessPaymentResponse;
    }

    @Override
    public UpdateStatusSuccessPaymentResponse updateSuccessStatusOfConsensus(Integer paymentId, UpdateStatusSuccessPaymentRequest updateStatusSuccessPaymentRequest) {
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.AWAITING_CONFIRMATION);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        User user = userService.getCurrentUser();
        ConsensusPaymentId id = new ConsensusPaymentId(paymentId, user.getUserId());

        ConsensusPayment consensusPayment = consensusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consensus payment not found"));


        if (consensusPayment.isSuccessAccepted() == updateStatusSuccessPaymentRequest.isSuccessAccepted()) {
            throw new IllegalArgumentException("Consensus payment process status have already " + consensusPayment.isSuccessAccepted());
        }

        consensusPayment.setSuccessAccepted(updateStatusSuccessPaymentRequest.isSuccessAccepted());

        consensusPayment.setUpdatedAt(LocalDateTime.now());

        ConsensusPayment saved = consensusRepository.save(consensusPayment);

        UpdateStatusSuccessPaymentResponse updateStatusSuccessPaymentResponse = new UpdateStatusSuccessPaymentResponse();

        updateStatusSuccessPaymentResponse.setPaymentId(paymentId);
        updateStatusSuccessPaymentResponse.setUpdatedAt(saved.getUpdatedAt().toString());
        updateStatusSuccessPaymentResponse.setSuccessAccepted(saved.isSuccessAccepted());

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
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.WAITING, PaymentRequestStatus.PROCESSING, PaymentRequestStatus.AWAITING_CONFIRMATION);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        User user = userService.getCurrentUser();
        if (user.getUserId() != existingPayment.getUser().getUserId()) {
            throw new AccessDeniedException("You don't have access to this payment request");
        }

        existingPayment.setStatus(PaymentRequestStatus.FAILED);

        return paymentRequestMapper.toPaymentResponse(paymentRequestRepository.save(existingPayment));
    }

    @Override
    public PaymentResponse changeStatusPaymentRequestToProcessing(Integer paymentId) {
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.WAITING);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        User user = userService.getCurrentUser();
        if (user.getUserId() != existingPayment.getUser().getUserId()) {
            throw new AccessDeniedException("You don't have access to this payment request");
        }

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

        User user = userService.getCurrentUser();
        if (user.getUserId() != existingPayment.getUser().getUserId()) {
            throw new AccessDeniedException("You don't have access to this payment request");
        }

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

        User creator = userService.getCurrentUser();
        if (creator.getUserId() != existingPayment.getUser().getUserId()) {
            throw new AccessDeniedException("You don't have access to this payment request");
        }

        Set<ConsensusPaymentResponse> consensusPaymentResponses = consensusService.getByPaymentId(paymentId);

        consensusPaymentResponses.forEach(res -> {
            if (!res.isSuccessAccepted()) {
                throw new IllegalArgumentException("Everyone didn't confirm this payment yet");
            }
        });

        existingPayment.setStatus(PaymentRequestStatus.READY_TO_SPLIT);

        double totalAmount = existingPayment.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceQuotation())
                .sum();

        existingPayment.setAmount(totalAmount);

        double eachShare = totalAmount / existingPayment.getConsensusPayments().size();

        for (ConsensusPayment consensusPayment : existingPayment.getConsensusPayments()) {
            User debtor = consensusPayment.getUser();
            if (debtor.getUserId() == creator.getUserId()) {
                continue;
            }

            UserDebt userDebt = UserDebt.builder()
                    .amount(eachShare)
                    .note("Debt from paymentId " + paymentId)
                    .createdAt(LocalDateTime.now())
                    .status(false)
                    .debtor(debtor)
                    .creditor(creator)
                    .payment(existingPayment)
                    .build();

            userDebtRepository.save(userDebt);
        }

        paymentRequestRepository.save(existingPayment);

        return paymentRequestMapper.toPaymentResponse(existingPayment);
    }

    @Override
    public void changeStatusPaymentRequestToSuccess(Integer paymentId) {
        Set<PaymentRequestStatus> allowedStatuses = Set.of(PaymentRequestStatus.READY_TO_SPLIT);
        Payment existingPayment = validatePaymentRequest(paymentId, allowedStatuses, null, null);

        existingPayment.setStatus(PaymentRequestStatus.SUCCESS);
    }

    @Override
    public void uploadPaymentRequestImage(MultipartFile file, Integer paymentId) {
        Payment payment = paymentRequestRepository.getByPaymentId(paymentId);

        String folder = "payment-request/" + paymentId;
        Map<String, Object> uploadResult = imageCloudinaryService.uploadImageFile(file, folder);
        payment.setImageUrl((String) uploadResult.get("secure_url"));
        payment.setImagePublicId((String) uploadResult.get("public_id"));
        paymentRequestRepository.save(payment);
    }
}
