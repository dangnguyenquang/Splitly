package com.example.splitly.application.service;

import com.example.splitly.application.mapper.PermissionMapper;
import com.example.splitly.application.serviceInterface.IPermissionService;
import com.example.splitly.domain.entity.Permission;
import com.example.splitly.domain.repository.PermissionRepository;
import com.example.splitly.presentation.dto.request.PermissionRequest;
import com.example.splitly.presentation.dto.response.PermissionResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        var permissionResponse = permissionRepository.save(permissionMapper.toPermission(permissionRequest));

        return permissionMapper.toPermissionResponse(permissionResponse);
    }

    public List<PermissionResponse> getAllPermission() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    public PermissionResponse getPermissionById(Integer permissionId) {
        return permissionMapper.toPermissionResponse(permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found in DB!")));
    }

    public void deletePermissionById(Integer permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
