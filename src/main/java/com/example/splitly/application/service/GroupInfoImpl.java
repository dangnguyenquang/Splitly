package com.example.splitly.application.service;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.GroupInfoRepository;
import com.example.splitly.service.InterfaceGroupInfo;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupInfoImpl implements InterfaceGroupInfo {

    private final GroupInfoRepository groupInfoRepository;
    private final UserRepository userRepository;

    @Override
    public List<GroupInfo> getAllGroup() {
        List<GroupInfo> groupInfos = new ArrayList<>();
        groupInfos.addAll(groupInfoRepository.findAll());
        return groupInfos;
    }

    @Override
    public GroupInfo createGroup(GroupDTO groupDTO) {
        GroupInfo groupInfo = GroupInfo.builder()
                .numberOfMember(groupDTO.getNumberOfMember())
                .groupName(groupDTO.getGroupName())
                .build();
        groupInfoRepository.save(groupInfo);
        return groupInfo;
    }

    @Override
    public GroupInfo updateGroup(Long groupId, GroupDTO groupDTO) {
        GroupInfo groupInfo = findGroupInfo(groupId);
        groupInfo.setGroupName(groupDTO.getGroupName());
        groupInfo.setNumberOfMember(groupDTO.getNumberOfMember());
        groupInfoRepository.save((groupInfo));
        return groupInfo;
    }

    @Override
    public void deleteGroup(Long groupId) {
        GroupInfo groupInfo = findGroupInfo(groupId);
        if (groupInfo != null) {
            groupInfoRepository.deleteById(groupId);
        }
    }

    @Override
    public GroupInfo findGroupInfo(Long groupId) {
        return groupInfoRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group not found!"));

    }

    @Override
    public void assignLeader(Long groupId, Integer userId) {
        GroupInfo groupInfo = findGroupInfo(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        groupInfo.setUser(user);
        groupInfoRepository.save(groupInfo);
    }

}
