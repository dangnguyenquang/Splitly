package com.example.splitly.presentation.dto.response;

import com.example.splitly.domain.enumerator.Gender;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class UserResponse implements Serializable {
    private int userId;

    private String fullName;

    private String phone;

    private String email;

    private Gender gender;

    private String userImage;

    private Set<RoleResponse> roles;
}
