package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.presentation.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<?> getAllUsers() {
        List<UserResponse> users = userService.getAll();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all users successfully", users);
    }

    @GetMapping("/{userId}")
    public ResponseData<?> getUserById(@PathVariable Integer userId) {
        UserResponse user = userService.getUserById(userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user successfully", user);
    }

    @GetMapping("/my-info")
    public ResponseData<?> getMyInfo() {
        UserResponse response = userService.getMyInfo();

        return new ResponseData<>(HttpStatus.OK.value(), "Get your info successfully", response);
    }

    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserRequest request) {
        UserResponse updatedUser = userService.updateUser(userId, request);
        return new ResponseData<>(HttpStatus.OK.value(), "User updated", updatedUser);
    }
}
