package com.example.splitly.application.service;

import com.example.splitly.application.mapper.AuthMapper;
import com.example.splitly.application.mapper.GroupInfoMapper;
import com.example.splitly.application.serviceInterface.IAuthService;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.RegisterRequest;
import com.example.splitly.presentation.dto.request.VerifyRequest;
import com.example.splitly.presentation.dto.response.AuthResponse;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import com.example.splitly.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupInfoMapper groupInfoMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public AuthResponse login(String email, String password) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException ex) {
            throw new UsernameNotFoundException("Account not found in database", ex);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }

        String jwt = jwtUtil.generateTokenFromPrincipal(userDetails.getUsername(), userDetails.getAuthorities());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<GroupInfo> groupInfos = groupUserRepository.findAllGroupsByUserId(user.getUserId());

        List<GroupInfoResponse> groups = groupInfoMapper.toGroupInfoResponses(groupInfos);

        return authMapper.toAuthResponse(user, jwt, groups);
    }

    @Override
    public void requestRegistration(RegisterRequest request) {
        // Check if a verified user with this email already exists
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    if (user.isVerified()) {
                        throw new IllegalStateException("Email already taken.");
                    }
                });

        // Either create a new user or update the existing unverified one
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(new User());

        user.setFullName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false); // Ensure user is marked as unverified

        userRepository.save(user);

        // Send OTP email
        sendOtp(user.getEmail());       
    }

    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        if (user.isVerified()) {
            throw new IllegalStateException("Email already taken.");
        }
        // Generate and set OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // 10-minute expiry
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp, 10);
    }

    /**
     * Step 2 of registration.
     * Verifies OTP and logs the user in upon success.
     */
    @Override
    public AuthResponse verifyRegistration(VerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this email."));

        // 1. Check if already verified
        if (user.isVerified()) {
            throw new IllegalStateException("Account is already verified.");
        }

        // 2. Check if OTP is expired
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("OTP has expired. Please request a new one.");
        }

        // 3. Check if OTP is correct
        if (!user.getOtp().equals(request.getOtp())) {
            throw new BadCredentialsException("Invalid OTP code.");
        }

        // --- Success ---
        // Mark user as verified and clear OTP fields
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        // Log the user in and return a token
        return generateAuthResponse(user.getEmail());
    }

    /**
     * Generates a 6-digit numeric OTP.
     */
    private String generateOtp() {
        Random random = new Random();
        int otpNumber = 100000 + random.nextInt(900000);
        return String.valueOf(otpNumber);
    }

    /**
     * Helper method to generate AuthResponse after successful login or
     * verification.
     */
    private AuthResponse generateAuthResponse(String email) {
        // We must re-fetch UserDetails to get authorities
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String jwt = jwtUtil.generateTokenFromPrincipal(userDetails.getUsername(), userDetails.getAuthorities());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<GroupInfo> groupInfos = groupUserRepository.findAllGroupsByUserId(user.getUserId());
        List<GroupInfoResponse> groups = groupInfoMapper.toGroupInfoResponses(groupInfos);

        return authMapper.toAuthResponse(user, jwt, groups);
    }
}
