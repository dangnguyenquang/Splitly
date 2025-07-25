package com.example.splitly.service;

import java.util.List;

import com.example.splitly.dto.request.GroupDTO;
import com.example.splitly.entity.GroupInfo;

public interface InterfaceGroupInfo {
    List<GroupInfo> getAllGroup();
    GroupInfo createGroup(GroupDTO groupDTO);
    GroupInfo updateGroup (Long groupId, GroupDTO groupDTO);
    void deleteGroup(Long groupId);
    GroupInfo findGroupInfo(Long groupId);
    void assignLeader(Long groupId, Integer userId);
}
