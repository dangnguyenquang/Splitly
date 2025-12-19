package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResendEmailRequest implements Serializable {
    @NotBlank(message = "Email can't be blank")
    private String email;
}
