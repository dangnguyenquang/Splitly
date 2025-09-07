package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse login(String email, String password);
}
