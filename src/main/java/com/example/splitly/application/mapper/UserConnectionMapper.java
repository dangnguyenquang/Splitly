package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.entity.UserConnection;
import com.example.splitly.presentation.dto.request.UserConnectionRequest;
import com.example.splitly.presentation.dto.response.UserConnectionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        imports = {
                java.time.LocalDateTime.class,
                com.example.splitly.domain.entity.UserConnectionId.class
        }
)
public interface UserConnectionMapper {

    @Mapping(target = "id",
            expression = "java(new UserConnectionId(dto.getRequestUserId(), dto.getReceiveUserId()))")
    @Mapping(target = "accepted", constant = "false")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    UserConnection toUserConnectionEntity(
            UserConnectionRequest dto,
            User requestUser,
            User receiveUser
    );

    /**
     * Maps UserConnection to UserConnectionResponse
     * Returns the connected user's information (not the current user)
     *
     * @param entity        The UserConnection entity
     * @param currentUserId The ID of the current user
     * @return UserConnectionResponse with the other user's details
     */
    default UserConnectionResponse toUserConnectionResponseDto(UserConnection entity, Integer currentUserId) {
        if (entity == null) {
            return null;
        }

        UserConnectionResponse response = new UserConnectionResponse();

        // Determine which user to return (the one that's not the current user)
        User connectedUser;
        UserConnectionResponse.ConnectionRole role;

        if (entity.getRequestUser().getUserId() == currentUserId) {
            // Current user is the requester, return receiver
            connectedUser = entity.getReceiveUser();
            role = UserConnectionResponse.ConnectionRole.REQUESTER;
        } else {
            // Current user is the receiver, return requester
            connectedUser = entity.getRequestUser();
            role = UserConnectionResponse.ConnectionRole.RECEIVER;
        }

        // Map connected user's details
        response.setUserId(connectedUser.getUserId());
        response.setUsername(connectedUser.getFullName());
        response.setEmail(connectedUser.getEmail());
        response.setFullName(connectedUser.getFullName());
        response.setAvatarUrl("");

        // Map connection details
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setAccepted(entity.isAccepted());
        response.setRole(role);

        return response;
    }
}