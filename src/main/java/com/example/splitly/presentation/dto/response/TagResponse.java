package com.example.splitly.presentation.dto.response;

import com.example.splitly.presentation.dto.request.PermissionRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TagResponse {
    private int tagId;

    private String tagName;

    private boolean isDeleted;
}