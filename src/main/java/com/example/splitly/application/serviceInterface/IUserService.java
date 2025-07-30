package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.PermissionRequest;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
    public List<UserResponse> getAll();

    public UserResponse getUserById(Integer userId);

    public UserResponse updateUser(Integer userId, UserRequest request);

    public UserResponse getMyInfo();
}
