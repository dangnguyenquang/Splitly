package com.example.splitly.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_connection")
@NoArgsConstructor
@AllArgsConstructor
public class UserConnection {

    @EmbeddedId
    private UserConnectionId id;

    @ManyToOne
    @MapsId("requestUserId")
    @JoinColumn(name = "request_user_id")
    private User requestUser;

    @ManyToOne
    @JoinColumn(name = "receive_user_id")
    @MapsId("receiveUserId")
    private User receiveUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_accepted")
    private boolean isAccepted;
}
