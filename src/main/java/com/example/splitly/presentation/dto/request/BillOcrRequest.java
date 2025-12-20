package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillOcrRequest {

    @NotNull(message = "Bill image is required")
    private MultipartFile image;

    private Integer paymentId;

    private String additionalContext; // Optional: restaurant name, date, etc.
}