package com.example.splitly.application.service;

import com.example.splitly.application.mapper.RoleMapper;
import com.example.splitly.application.serviceInterface.IRoleService;
import com.example.splitly.domain.repository.PermissionRepository;
import com.example.splitly.domain.repository.RoleRepository;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest roleRequest) {
        var role = roleMapper.toRole(roleRequest);

        var permissions = permissionRepository.findAllByPermissionIdIn(roleRequest.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void delete(Integer roleId) {
        roleRepository.deleteById(roleId);
    }
}
