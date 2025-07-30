package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.domain.entity.User;

public interface IGroupUser {
    void inviteUserToGroup(Integer userId, Long groupId);
    void removeUserOutGroup(Integer userId, Long groupId, Integer leaderId);
    List<User> getAllUserGroup(Long groupId);

}
