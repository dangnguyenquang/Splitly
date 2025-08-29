package com.example.splitly.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.splitly.domain.enumerator.InvitationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
