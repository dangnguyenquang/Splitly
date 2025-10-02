package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.User;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;


public interface IGroupInfo {
    List<GroupInfoResponse> getAllGroup();

    GroupInfoResponse createGroup(String groupName, List<String> emailList);

    GroupInfoResponse updateGroup(Long groupId, GroupDTO groupDTO);

    void deleteGroup(Long groupId);

    GroupInfoResponse findGroupInfoResponse(Long groupId);

    GroupInfo findGroupInfo(Long groupId);
    public User findLeader(Long groupId);

    void assignLeader(Long groupId, Integer userId);
}
