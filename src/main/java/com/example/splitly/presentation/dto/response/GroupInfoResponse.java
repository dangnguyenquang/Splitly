package com.example.splitly.presentation.dto.response;

import com.example.splitly.domain.entity.User;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class GroupInfoResponse implements Serializable {
    private Integer numberOfMember;
    private String groupName;
    private Long groupId;
    private UserResponse leader;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
