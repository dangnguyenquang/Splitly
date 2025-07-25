package com.example.splitly.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GroupUser {

    @EmbeddedId
    private GroupUserId groupUserId;

    @ManyToOne
    @JoinColumn(name = "group_id", insertable = false, updatable = false, nullable = false)
    private GroupInfo groupInfo;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private User user;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "john_at")
    private LocalDateTime johnAt;
}
