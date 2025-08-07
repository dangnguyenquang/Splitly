package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.presentation.dto.request.GroupDTO;



public interface IGroupInfo {
    List<GroupInfo> getAllGroup();
    GroupInfo createGroup(GroupDTO groupDTO, Integer userId, List<String> emailList);
    GroupInfo updateGroup (Long groupId, GroupDTO groupDTO, Integer leaderId);
    void deleteGroup(Long groupId, Integer leaderId);
    GroupInfo findGroupInfo(Long groupId);
    void assignLeader(Long groupId, Integer userId);
}
