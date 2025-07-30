package com.example.splitly.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class GroupUserId {
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private Integer userId;
}
