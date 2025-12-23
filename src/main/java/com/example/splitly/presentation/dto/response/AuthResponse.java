package com.example.splitly.presentation.dto.response;

import com.example.splitly.domain.enumerator.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private String userImage;
    private List<GroupInfoResponse> groups;
}
