package com.example.splitly.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentUserResponse implements Serializable {
    private int userId;

    private String fullName;
}
