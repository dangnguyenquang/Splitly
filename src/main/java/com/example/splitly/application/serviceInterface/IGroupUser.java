package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.User;

public interface IGroupUser {
    void inviteUserToGroup(String email, Long groupId);
    void removeUserOutGroup(Integer userId, Long groupId, Integer leaderId);
    List<User> getAllUserGroup(Long groupId);
    List<GroupInfo> getGroupsByUserId(Integer userId);
    void deleteGroupUser(Long groupId);

}
