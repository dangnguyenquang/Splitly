package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.RegisterRequest;
import com.example.splitly.presentation.dto.request.ResendEmailRequest;
import com.example.splitly.presentation.dto.request.VerifyRequest;
import com.example.splitly.presentation.dto.response.AuthResponse;
import org.apache.coyote.BadRequestException;

public interface IAuthService {
    AuthResponse login(String email, String password);

    void requestRegistration(RegisterRequest request);

    AuthResponse verifyRegistration(VerifyRequest request);

    void resendOtp(ResendEmailRequest request) throws BadRequestException;
}
