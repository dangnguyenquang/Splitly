package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Role;
import com.example.splitly.domain.entity.User;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;
import com.example.splitly.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    User toUser(UserRequest userRequest);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User existingUser, UserRequest request);
}
