package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.application.serviceInterface.IGroupInfo;
import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.InvitationStatus;
import com.example.splitly.domain.repository.GroupInfoRepository;
import com.example.splitly.domain.repository.GroupUserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupInfoImpl implements IGroupInfo {

    private final GroupInfoRepository groupInfoRepository;
    private final UserRepository userRepository;
    private final IGroupUser iGroupUser;
    private final GroupUserRepository groupUserRepository;

    @Override
    public List<GroupInfo> getAllGroup() {
        List<GroupInfo> groupInfos = new ArrayList<>();
        groupInfos.addAll(groupInfoRepository.findAll());
        return groupInfos;
    }

    @Override
    public GroupInfo createGroup(GroupDTO groupDTO, Integer userId, List<String> emailList) {
        GroupInfo groupInfo = GroupInfo.builder()
                .numberOfMember(groupDTO.getNumberOfMember())
                .groupName(groupDTO.getGroupName())
                .build();
        groupInfoRepository.save(groupInfo);
        assignLeader(groupInfo.getGroupId(), userId);
        GroupUserId groupUserId = GroupUserId.builder()
                        .groupId(groupInfo.getGroupId())
                        .userId(userId)
                        .build();
        GroupUser groupUser = GroupUser.builder()
                        .groupUserId(groupUserId)
                        .groupInfo(groupInfo)
                        .user(groupInfo.getUser())
                        .joinedAt(LocalDateTime.now())
                        .status(InvitationStatus.SUCCESS)
                    .build();
        groupUserRepository.save(groupUser);
        if (!emailList.isEmpty()) {
            for (String email : emailList) {
                try {
                    if (email != groupInfo.getUser().getEmail()) {
                        iGroupUser.inviteUserToGroup(email, groupInfo.getGroupId());
                    }
                } catch (EntityNotFoundException e) {
                    System.out.println("Email not found: " + email);
                }
            }
        }
        return groupInfo;
    }

    @Override
    public GroupInfo updateGroup(Long groupId, GroupDTO groupDTO, Integer leaderId) {
        GroupInfo groupInfo = findGroupInfo(groupId);
        if (groupInfo.getUser().getUserId() == leaderId) {
            groupInfo.setGroupName(groupDTO.getGroupName());
            groupInfo.setNumberOfMember(groupDTO.getNumberOfMember());
            groupInfoRepository.save((groupInfo));
        } else {
            throw new EntityNotFoundException("User is not a leader");
        }
        return groupInfo;
    }

    @Override
    public void deleteGroup(Long groupId, Integer leaderId) {
        iGroupUser.deleteGroupUser(groupId);
        GroupInfo groupInfo = findGroupInfo(groupId);
        if (groupInfo != null && groupInfo.getUser().getUserId() == leaderId) {
            groupInfo.setUser(null);
            groupInfoRepository.save(groupInfo);
            groupInfoRepository.deleteById(groupId); 
        } else {
            throw new EntityNotFoundException("Group not found!");
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
