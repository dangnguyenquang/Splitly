package com.example.splitly.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class TagRequest implements Serializable {
    private int tagId;

    @NotBlank(message = "Role name can't be blank")
    private String tagName;

    private boolean isDeleted;
}
