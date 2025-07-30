package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ConsensusMapper;
import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.domain.entity.ConsensusPayment;
import com.example.splitly.domain.entity.ConsensusPaymentId;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.ConsensusRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.response.ConsensusPaymentResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsensusService implements IConsensusService {
    private final ConsensusRepository consensusRepository;
    private final ConsensusMapper consensusMapper;
    private final UserRepository userRepository;

    @Override
    public ConsensusPaymentResponse create(ConsensusPaymentRequest consensusPaymentRequest, Payment payment) {
        User user = userRepository.findById(consensusPaymentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ConsensusPayment consensusPayment = new ConsensusPayment();

        ConsensusPaymentId id = new ConsensusPaymentId(user.getUserId(), payment.getPaymentId());

        consensusPayment.setConsensusPaymentId(id);
        consensusPayment.setPayment(payment);
        consensusPayment.setUser(user);
        consensusPayment.setCreatedAt(LocalDateTime.now());
        consensusPayment.setUpdatedAt(LocalDateTime.now());
        consensusPayment.setAccepted(consensusPaymentRequest.isAccepted());

        return consensusMapper.toConsensusPaymentResponse(consensusRepository.save(consensusPayment));
    }

    @Override
    public Set<ConsensusPaymentResponse> createAll(Set<ConsensusPaymentRequest> consensusPaymentRequests, Payment payment) {
        if (consensusPaymentRequests == null || consensusPaymentRequests.isEmpty()) {
            throw new IllegalArgumentException("Item request set cannot be null or empty.");
        }

        Set<Integer> userIds = consensusPaymentRequests.stream()
                .map(ConsensusPaymentRequest::getUserId)
                .collect(Collectors.toSet());

        Map<Integer, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        Set<ConsensusPayment> consensusPayments = consensusPaymentRequests.stream().map(consensusPaymentRequest -> {
            User user = userMap.get(consensusPaymentRequest.getUserId());

            if (user == null || payment == null) {
                throw new EntityNotFoundException("User or Payment not found");
            }


            ConsensusPayment consensusPayment = new ConsensusPayment();

            ConsensusPaymentId id = new ConsensusPaymentId(user.getUserId(), payment.getPaymentId());

            consensusPayment.setConsensusPaymentId(id);
            consensusPayment.setPayment(payment);
            consensusPayment.setUser(user);
            consensusPayment.setCreatedAt(LocalDateTime.now());
            consensusPayment.setUpdatedAt(LocalDateTime.now());
            consensusPayment.setAccepted(consensusPaymentRequest.isAccepted());


            if (consensusPayment.getPayment() == null) {
                throw new IllegalArgumentException("Each item must be associated with a payment.");
            }

            return consensusPayment;
        }).collect(Collectors.toSet());

        Set<ConsensusPayment> savedConsensusPayment = new HashSet<>(consensusRepository.saveAll(consensusPayments));
        return savedConsensusPayment.stream()
                .map(consensusMapper::toConsensusPaymentResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public ConsensusPaymentResponse update(Integer consensusPaymentId, ConsensusPaymentRequest consensusPaymentRequest) {
        return null;
    }

    @Override
    public Set<ConsensusPaymentResponse> getByPaymentId(Integer paymentId) {
        return Set.of();
    }
}
