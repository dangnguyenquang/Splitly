package com.example.splitly.application.service;

import com.example.splitly.application.facade.notification.NotificationFacade;
import com.example.splitly.application.mapper.UserConnectionMapper;
import com.example.splitly.application.serviceInterface.IUserConnection;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.entity.UserConnection;
import com.example.splitly.domain.repository.UserConnectionRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.UserConnectionRequest;
import com.example.splitly.presentation.dto.response.UserConnectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserConnectionService implements IUserConnection {
    private final UserConnectionRepository userConnectionRepository;
    private final UserRepository userRepository;
    private final UserConnectionMapper mapper;
    private final UserService userService;
    private final NotificationFacade notificationFacade;

    @Override
    @Transactional
    public UserConnectionResponse createConnection(UserConnectionRequest dto) {
        User currentUser = userService.getCurrentUser();

        // Prevent self-connection
        if (currentUser.getUserId() == dto.getReceiveUserId()) {
            throw new RuntimeException("Cannot create connection with yourself");
        }

        // Check if connection already exists (in either direction)
        boolean connectionExists = userConnectionRepository
                .existsByIdRequestUserIdAndIdReceiveUserId(currentUser.getUserId(), dto.getReceiveUserId()) ||
                userConnectionRepository
                        .existsByIdRequestUserIdAndIdReceiveUserId(dto.getReceiveUserId(), currentUser.getUserId());

        if (connectionExists) {
            throw new RuntimeException("Connection already exists between these users");
        }

        User receiveUser = userRepository.findById(dto.getReceiveUserId())
                .orElseThrow(() -> new RuntimeException("Receive user not found"));

        UserConnection connection = mapper.toUserConnectionEntity(dto, currentUser, receiveUser);

        log.info("User {} created connection request to user {}",
                currentUser.getUserId(), dto.getReceiveUserId());

        notificationFacade.notifyConnectionRequestReceived(currentUser, receiveUser);

        return mapper.toUserConnectionResponseDto(userConnectionRepository.save(connection), currentUser.getUserId());
    }

    @Override
    public List<UserConnectionResponse> getAllConnections() {
        User currentUser = userService.getCurrentUser();

        // Return only connections where current user is involved
        List<UserConnection> connections = userConnectionRepository
                .findByIdRequestUserIdOrIdReceiveUserId(currentUser.getUserId(), currentUser.getUserId());

        log.info("Retrieved {} connections for user {}", connections.size(), currentUser.getUserId());

        return connections.stream()
                .map(c -> mapper.toUserConnectionResponseDto(c, currentUser.getUserId()))
                .toList();
    }

    @Override
    public List<UserConnectionResponse> getConnectionsByStatus(boolean isAccepted) {
        User currentUser = userService.getCurrentUser();

        // Return only connections where current user is involved and match the status
        List<UserConnection> connections = userConnectionRepository
                .findByIsAcceptedAndIdRequestUserIdOrIdReceiveUserId(
                        isAccepted,
                        currentUser.getUserId(),
                        currentUser.getUserId()
                );

        log.info("Retrieved {} {} connections for user {}",
                connections.size(),
                isAccepted ? "accepted" : "pending",
                currentUser.getUserId());

        return connections.stream()
                .map(c -> mapper.toUserConnectionResponseDto(c, currentUser.getUserId()))
                .toList();
    }

    @Override
    @Transactional
    public UserConnectionResponse acceptConnection(Integer requestUserId) {
        return changeConnectionStatus(requestUserId, true);
    }

    @Override
    @Transactional
    public UserConnectionResponse changeConnectionStatus(
            Integer requestUserId,
            boolean status
    ) {
        User currentUser = userService.getCurrentUser();

        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new RuntimeException("Request user not found"));

        UserConnection connection = userConnectionRepository
                .findConnectionBetweenUsers(requestUserId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        connection.setAccepted(status);
        connection.setUpdatedAt(LocalDateTime.now());

        log.info("User {} changed connection status to {} for request from user {}",
                currentUser.getUserId(), status, requestUserId);

        if (status) {
            notificationFacade.notifyConnectionRequestAccepted(requestUser, currentUser);
        } else {
            notificationFacade.notifyConnectionRequestRejected(requestUser, currentUser);
        }

        return mapper.toUserConnectionResponseDto(userConnectionRepository.save(connection), currentUser.getUserId());
    }

    @Transactional
    @Override
    public void deleteConnection(Integer userId) {
        User currentUser = userService.getCurrentUser();

        UserConnection connection = userConnectionRepository
                .findConnectionBetweenUsers(userId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        userConnectionRepository.delete(connection);
    }

    @Override
    public List<UserConnectionResponse> getPendingReceivedRequests() {
        User currentUser = userService.getCurrentUser();

        // Get all pending connections where current user is the receiver
        List<UserConnection> pendingRequests = userConnectionRepository
                .findByIdReceiveUserIdAndIsAccepted(currentUser.getUserId(), false);

        log.info("Retrieved {} pending received requests for user {}",
                pendingRequests.size(), currentUser.getUserId());

        return pendingRequests.stream()
                .map(c -> mapper.toUserConnectionResponseDto(c, currentUser.getUserId()))
                .toList();
    }

    @Override
    public List<UserConnectionResponse> getPendingSentRequests() {
        User currentUser = userService.getCurrentUser();

        // Get all pending connections where current user is the requester
        List<UserConnection> sentRequests = userConnectionRepository
                .findByIdRequestUserIdAndIsAccepted(currentUser.getUserId(), false);

        log.info("Retrieved {} pending sent requests for user {}",
                sentRequests.size(), currentUser.getUserId());

        return sentRequests.stream()
                .map(c -> mapper.toUserConnectionResponseDto(c, currentUser.getUserId()))
                .toList();
    }
}