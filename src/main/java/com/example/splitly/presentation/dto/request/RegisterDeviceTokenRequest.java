package com.example.splitly.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RegisterDeviceTokenRequest {
    private String deviceId;
    private String token;
    private String platform; // "ANDROID"
}
