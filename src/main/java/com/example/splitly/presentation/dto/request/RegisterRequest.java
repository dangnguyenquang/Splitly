package com.example.splitly.presentation.dto.request;

import com.example.splitly.domain.enumerator.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RegisterRequest implements Serializable {
    @NotBlank(message = "Username can't be blank")
    private String userName;

    @NotBlank(message = "Phone number can't be blank")
    private String phoneNumber;

    @NotNull(message = "Gender can't be null")
    private Gender gender;

    @NotBlank(message = "Email can't be blank")
    private String email;

    @NotBlank(message = "Password can't be blank")
    private String password;
}
