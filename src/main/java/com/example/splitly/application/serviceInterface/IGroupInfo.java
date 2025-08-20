package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.presentation.dto.request.GroupDTO;



public interface IGroupInfo {
    List<GroupInfo> getAllGroup();
    GroupInfo createGroup(GroupDTO groupDTO);
    GroupInfo updateGroup (Long groupId, GroupDTO groupDTO);
    void deleteGroup(Long groupId);
    GroupInfo findGroupInfo(Long groupId);
    void assignLeader(Long groupId, Integer userId);
}
