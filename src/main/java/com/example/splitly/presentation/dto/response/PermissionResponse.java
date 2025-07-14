package com.example.splitly.presentation.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PermissionResponse implements Serializable {
    private int permissionId;

    private String permissionName;
}
