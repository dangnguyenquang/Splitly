package com.example.splitly.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserConnectionResponse {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isAccepted;
    private ConnectionRole role; // REQUESTER or RECEIVER

    public enum ConnectionRole {
        REQUESTER, // Current user sent the request
        RECEIVER   // Current user received the request
    }
}