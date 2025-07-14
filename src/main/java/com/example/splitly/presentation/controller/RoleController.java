package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IRoleService;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.presentation.dto.response.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final IRoleService roleService;

    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid RoleRequest request) {
        RoleResponse response = roleService.create(request);

        return new ResponseData<>(HttpStatus.OK.value(), "Permission saved successfully", response);
    }

    @GetMapping
    public ResponseData<?> getAll() {
        List<RoleResponse> response = roleService.getAll();

        return new ResponseData<>(HttpStatus.OK.value(), "Get permissions successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Integer id) {
        roleService.delete(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Permission deleted");
    }
}
