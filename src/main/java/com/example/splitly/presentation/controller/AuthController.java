package com.example.splitly.presentation.controller;

import com.example.splitly.application.service.CustomUserDetailsService;
import com.example.splitly.application.serviceInterface.IAuthService;
import com.example.splitly.presentation.dto.request.AuthDTO;
import com.example.splitly.presentation.dto.response.AuthResponse;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseData<?>> login(@RequestBody AuthDTO request) {
        try {
            AuthResponse authResponse = authService.login(request.getEmail(), request.getPassword());

            return ResponseEntity.ok(
                    new ResponseData<>(HttpStatus.OK.value(), "Login successfully", authResponse)
            );

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
}
