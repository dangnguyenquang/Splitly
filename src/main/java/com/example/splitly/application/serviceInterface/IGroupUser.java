package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import com.example.splitly.presentation.dto.response.UserResponse;

public interface IGroupUser {
    void inviteUserToGroup(String email, Long groupId);

    void removeUserOutGroup(Integer userId, Long groupId);

    List<UserResponse> getAllUserGroup(Long groupId);

    List<GroupInfoResponse> getGroupsByUserId();

    void deleteGroupUser(Long groupId);

    boolean areUsersInGroup(Long groupId, List<Integer> userIds);
}
