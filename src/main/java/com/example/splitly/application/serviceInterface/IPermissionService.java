package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.PermissionRequest;
import com.example.splitly.presentation.dto.response.PermissionResponse;

import java.util.List;

public interface IPermissionService {
    public PermissionResponse createPermission(PermissionRequest permissionRequest);

    public List<PermissionResponse> getAllPermission();

    public PermissionResponse getPermissionById(Integer permission_id);

    public void deletePermissionById(Integer permission_id);
}
