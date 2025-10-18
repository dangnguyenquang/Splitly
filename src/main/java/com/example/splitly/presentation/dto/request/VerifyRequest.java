package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank(message = "Email can't be blank")
    @Email
    private String email;

    @NotBlank(message = "OTP code can't be blank")
    private String otp;
}
