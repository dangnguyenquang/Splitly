package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Permission;
import com.example.splitly.presentation.dto.request.PermissionRequest;
import com.example.splitly.presentation.dto.response.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}
