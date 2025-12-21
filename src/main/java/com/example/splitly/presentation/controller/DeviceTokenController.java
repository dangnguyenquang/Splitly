package com.example.splitly.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.splitly.application.service.DeviceTokenService;
import com.example.splitly.presentation.dto.request.RegisterDeviceTokenRequest;
import com.example.splitly.presentation.dto.response.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping("/register")
    public ResponseEntity<ResponseData<?>> register(
            @RequestBody RegisterDeviceTokenRequest registerDeviceTokenRequest) {
        deviceTokenService.register(registerDeviceTokenRequest);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Register device token succesfully"));

    }
}
