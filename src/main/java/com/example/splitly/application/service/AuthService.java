package com.example.splitly.application.service;

import com.example.splitly.application.mapper.AuthMapper;
import com.example.splitly.application.mapper.GroupInfoMapper;
import com.example.splitly.application.serviceInterface.IAuthService;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.domain.repository.UserRepository;
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

import java.util.List;

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
}
