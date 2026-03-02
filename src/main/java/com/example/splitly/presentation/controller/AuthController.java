package com.example.splitly.presentation.controller;

import com.example.splitly.application.service.CustomUserDetailsService;
import com.example.splitly.application.service.EmailService;
import com.example.splitly.application.serviceInterface.IAuthService;
import com.example.splitly.presentation.dto.request.AuthDTO;
import com.example.splitly.presentation.dto.request.NewPasswordRequest;
import com.example.splitly.presentation.dto.request.RegisterRequest;
import com.example.splitly.presentation.dto.request.ResendEmailRequest;
import com.example.splitly.presentation.dto.request.VerifyRequest;
import com.example.splitly.presentation.dto.response.AuthResponse;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.security.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, registration, and password management")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseData<?>> login(@RequestBody AuthDTO request) {
        try {
            AuthResponse authResponse = authService.login(request.getEmail(), request.getPassword());

            return ResponseEntity.ok(
                    new ResponseData<>(HttpStatus.OK.value(), "Login successfully", authResponse));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseData<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseData<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseData<?>> registerUser(@Valid @RequestBody RegisterRequest request) {
        authService.requestRegistration(request);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(),
                        "Registration request successful. Please check your email for the OTP."));
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseData<?>> verifyUser(@Valid @RequestBody VerifyRequest request) {
        AuthResponse authResponse = authService.verifyRegistration(request);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(), "Verify successfully", authResponse));
    }

    @PostMapping("/resend-otp/{email}")
    public ResponseEntity<ResponseData<?>> resendOTP(@PathVariable String email) {
        authService.sendOtp(email);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Resend succesfully"));
    }

    @PostMapping("/verify-reset")
    public ResponseEntity<ResponseData<?>> verifyResetPasswordUser(@Valid @RequestBody VerifyRequest request) {
        String token = authService.verifyResetPasswordOtp(request);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(), "Verify successfully", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<?>> resetPasswordUser(@Valid @RequestBody NewPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(), "Reset password successfully"));
    }
}
