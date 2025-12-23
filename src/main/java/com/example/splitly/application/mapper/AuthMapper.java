package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.User;
import com.example.splitly.presentation.dto.response.AuthResponse;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { GroupInfoMapper.class })
public interface AuthMapper {
    @Mapping(target = "token", source = "token")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "gender", source = "user.gender")
    @Mapping(target = "groups", source = "groups")
    @Mapping(target = "userImage", source = "user.userImage")
    AuthResponse toAuthResponse(User user, String token, List<GroupInfoResponse> groups);
}
