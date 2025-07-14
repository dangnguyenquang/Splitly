package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IPermissionService;
import com.example.splitly.presentation.dto.request.PermissionRequest;
import com.example.splitly.presentation.dto.response.PermissionResponse;
import com.example.splitly.presentation.dto.response.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Validated
public class PermissionController {

    private final IPermissionService permissionService;

    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid PermissionRequest request) {
        PermissionResponse response = permissionService.createPermission(request);

        return new ResponseData<>(HttpStatus.OK.value(), "Permission saved successfully", response);
    }

    @GetMapping
    public ResponseData<?> getAll() {
        List<PermissionResponse> response = permissionService.getAllPermission();

        return new ResponseData<>(HttpStatus.OK.value(), "Get permissions successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getPermissionById(@PathVariable Integer id) {
        PermissionResponse response = permissionService.getPermissionById(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Get permission by id successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Integer id) {
        permissionService.deletePermissionById(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Permission deleted");
    }
}
