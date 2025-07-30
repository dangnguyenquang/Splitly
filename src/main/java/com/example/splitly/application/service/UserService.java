package com.example.splitly.application.service;

import ch.qos.logback.core.spi.ErrorCodes;
import com.example.splitly.application.mapper.RoleMapper;
import com.example.splitly.application.mapper.UserMapper;
import com.example.splitly.application.serviceInterface.IRoleService;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.Role;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.PermissionRepository;
import com.example.splitly.domain.repository.RoleRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;
import com.example.splitly.presentation.dto.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in DB")));
    }

    @Override
    public UserResponse updateUser(Integer userId, UserRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in DB"));

        userMapper.updateUser(existingUser, request);
        Set<Integer> roleIds = request.getRoles().stream()
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        List<Role> roles = roleRepository.findAllById(roleIds);

        existingUser.setFullName(request.getFullName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhone(request.getPhone());
        existingUser.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(existingUser));
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByEmail(name).orElseThrow(() -> new ApplicationContextException("")); // This code will be update soon when we have application exception code

        return userMapper.toUserResponse(user);
    }
}
