package com.example.splitly.service;

import java.util.List;

import com.example.splitly.entity.User;

public interface IGroupUser {
    void inviteUserToGroup(Integer userId, Long groupId);
    void removeUserOutGroup(Integer userId, Long groupId, Integer leaderId);
    List<User> getAllUserGroup(Long groupId);

}
