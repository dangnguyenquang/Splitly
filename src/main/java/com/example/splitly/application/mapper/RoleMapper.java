package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Role;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);
}
