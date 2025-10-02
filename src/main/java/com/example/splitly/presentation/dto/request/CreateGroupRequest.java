package com.example.splitly.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank(message = "Group name must not be blank")
    private String groupName;

    @Size(max = 5, message = "At most 5 emails per request")
    @NotEmpty(message = "List of emails must not be empty")
    private List<@Email @NotBlank String> emailList;
}
