package com.example.splitly.presentation.controller;

import com.example.splitly.application.service.CustomUserDetailsService;
import com.example.splitly.presentation.dto.request.AuthDTO;
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
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ResponseData<?>> login(@RequestBody AuthDTO request) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        } catch (UsernameNotFoundException ex) {
            ResponseData<?> response = new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Account not found in database",
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            ResponseData<?> response = new ResponseData<>(HttpStatus.UNAUTHORIZED.value(),
                    "Invalid email or password", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        final String jwt = jwtUtil.generateTokenFromPrincipal(userDetails.getUsername(), userDetails.getAuthorities()); // getUserName
                                                                                                                        // <=>
                                                                                                                        // getEmail
        Map<String, String> data = new HashMap<>();
        data.put("token", jwt);

        ResponseData<?> response = new ResponseData<>(HttpStatus.OK.value(), "Login successfully", data);
        return ResponseEntity.ok(response);
    }
}
