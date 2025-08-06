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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsensusService implements IConsensusService {
    private final ConsensusRepository consensusRepository;
    private final ConsensusMapper consensusMapper;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

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
                .map(consensusPayment -> {
                    ConsensusPaymentResponse consensusPaymentResponse = consensusMapper.toConsensusPaymentResponse(consensusPayment);
                    User user = customUserDetailsService.findUserById(consensusPayment.getUser().getUserId());

                    consensusPaymentResponse.setPhone(user.getPhone());
                    consensusPaymentResponse.setFullName(user.getFullName());
                    consensusPaymentResponse.setEmail(user.getEmail());

                    return consensusPaymentResponse;
                })
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Set<ConsensusPaymentResponse> updateConsensusPaymentById(Payment payment, Set<ConsensusPaymentRequest> consensusPaymentRequests) {
        Set<ConsensusPayment> oldConsensusPayments = consensusRepository.findByPaymentPaymentId(payment.getPaymentId());

        for (ConsensusPayment cp : oldConsensusPayments) {
            if (cp.getConsensusPaymentId() == null) {
                cp.setConsensusPaymentId(new ConsensusPaymentId(cp.getPayment().getPaymentId(), cp.getUser().getUserId()));
            }
        }

        Map<ConsensusPaymentId, ConsensusPayment> oldMap = oldConsensusPayments.stream()
                .collect(Collectors.toMap(
                        ConsensusPayment::getConsensusPaymentId,
                        Function.identity()
                ));

        List<ConsensusPayment> toSave = new ArrayList<>();


        for (ConsensusPaymentRequest request : consensusPaymentRequests) {
            int userId = request.getUserId();
            int paymentId = request.getPaymentId();

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            ConsensusPaymentId cpId = new ConsensusPaymentId(userId, paymentId);

            if (oldMap.containsKey(cpId)) {
                ConsensusPayment existing = oldMap.get(cpId);

                consensusMapper.updateConsensus(existing, request);

                existing.setPayment(payment);
                existing.setUser(user);
                existing.setUpdatedAt(LocalDateTime.now());

                toSave.add(existing);
                oldMap.remove(cpId);
            } else {
                ConsensusPayment newCP = consensusMapper.toConsensusPayment(request);
                newCP.setUser(user);
                newCP.setConsensusPaymentId(cpId);
                newCP.setPayment(payment);
                newCP.setCreatedAt(LocalDateTime.now());
                newCP.setUpdatedAt(LocalDateTime.now());

                toSave.add(newCP);
            }
        }

        consensusRepository.deleteAll(oldMap.values());

        List<ConsensusPayment> saved = consensusRepository.saveAll(toSave);

        return saved.stream().map(consensusMapper::toConsensusPaymentResponse).collect(Collectors.toSet());
    }

    @Override
    public ConsensusPaymentResponse update(Integer consensusPaymentId, ConsensusPaymentRequest consensusPaymentRequest) {
        return null;
    }

    @Override
    public Set<ConsensusPaymentResponse> getByPaymentId(Integer paymentId) {
        return consensusRepository.findByPaymentPaymentId(paymentId).stream().map(consensusPayment -> {
            ConsensusPaymentResponse consensusPaymentResponse = consensusMapper.toConsensusPaymentResponse(consensusPayment);
            User user = customUserDetailsService.findUserById(consensusPayment.getUser().getUserId());

            consensusPaymentResponse.setPhone(user.getPhone());
            consensusPaymentResponse.setFullName(user.getFullName());
            consensusPaymentResponse.setEmail(user.getEmail());

            return consensusPaymentResponse;
        }).collect(Collectors.toSet());
    }
}
