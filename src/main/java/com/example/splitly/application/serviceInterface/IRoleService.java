package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    public RoleResponse create(RoleRequest roleRequest);

    public List<RoleResponse> getAll();

    public void delete(Integer roleId);
}
