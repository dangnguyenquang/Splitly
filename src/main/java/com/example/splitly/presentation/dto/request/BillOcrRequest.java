package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillOcrRequest {

    @NotEmpty(message = "At least one bill image is required")
    @Size(max = 10, message = "Maximum 10 images allowed per request")
    private List<MultipartFile> images;

    private String additionalContext;
}