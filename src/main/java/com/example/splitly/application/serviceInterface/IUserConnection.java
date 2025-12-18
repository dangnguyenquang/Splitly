package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.UserConnectionRequest;
import com.example.splitly.presentation.dto.response.UserConnectionResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUserConnection {
    UserConnectionResponse createConnection(UserConnectionRequest dto);

    List<UserConnectionResponse> getAllConnections();

    List<UserConnectionResponse> getConnectionsByStatus(boolean isAccepted);

    UserConnectionResponse acceptConnection(
            Integer requestUserId,
            Integer receiveUserId
    );

    UserConnectionResponse changeConnectionStatus(
            Integer requestUserId,
            Integer receiveUserId,
            boolean status
    );

    @Transactional
    void deleteConnection(Integer requestUserId, Integer receiveUserId);

    List<UserConnectionResponse> getPendingReceivedRequests();

    List<UserConnectionResponse> getPendingSentRequests();
}
