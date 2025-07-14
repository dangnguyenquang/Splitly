package com.example.splitly.presentation.dto.request;

import com.example.splitly.domain.entity.Role;
import com.example.splitly.domain.enumerator.Gender;
import com.example.splitly.presentation.dto.response.RoleResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserRequest implements Serializable {
    @NotBlank(message = "Full name can't be blank")
    private String fullName;

    @NotBlank(message = "Phone can't be blank")
    private String phone;

    @NotBlank(message = "Email can't be blank")
    private String email;

    @NotBlank(message = "Gender can't be blank")
    private Gender gender;

    private Set<String> roles;
}
