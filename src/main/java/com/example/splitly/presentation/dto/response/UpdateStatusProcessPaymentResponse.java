package com.example.splitly.presentation.dto.response;

import com.example.splitly.domain.enumerator.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UpdateStatusProcessPaymentResponse implements Serializable {
    private Integer paymentId;

    private boolean processAccepted;

    private String updatedAt;
}
