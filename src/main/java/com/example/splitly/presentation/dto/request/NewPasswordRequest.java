package com.example.splitly.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordRequest {
    private String password;
    private String resetToken;
    
}
