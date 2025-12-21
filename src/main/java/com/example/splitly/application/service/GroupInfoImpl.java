package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.*;
import org.springframework.stereotype.Service;

import com.example.splitly.presentation.dto.request.CreateGroupRequest;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import com.example.splitly.application.mapper.GroupInfoMapper;
import com.example.splitly.application.serviceInterface.IGroupInfo;
import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.InvitationStatus;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupInfoImpl implements IGroupInfo {

    private final GroupInfoRepository groupInfoRepository;
    private final UserRepository userRepository;
    private final IGroupUser iGroupUser;
    private final GroupUserRepository groupUserRepository;
    private final IUserService iUserService;
    private final GroupInfoMapper groupInfoMapper;
    private final PaymentRequestRepository paymentRequestRepository;
    private final UserDebtRepository userDebtRepository;
    @Override
    public List<GroupInfoResponse> getAllGroup() {
        List<GroupInfo> groupInfos = new ArrayList<>();
        groupInfos.addAll(groupInfoRepository.findAll());
        return groupInfoMapper.toGroupInfoResponses(groupInfos);
    }

    @Override
    public GroupInfoResponse createGroup(CreateGroupRequest request) {
        User user = iUserService.getCurrentUser();
        if (request.getEmailList().contains(user.getEmail())) {
            throw new IllegalArgumentException("You cannot invite your own email");
        }
        GroupInfo groupInfo = GroupInfo.builder()
                .numberOfMember(100)
                .groupName(request.getGroupName())
                .descriptions(request.getDescriptions())
                .currency(request.getCurrency())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        groupInfoRepository.save(groupInfo);
        assignLeader(groupInfo.getGroupId(), user.getUserId());
        GroupUserId groupUserId = GroupUserId.builder()
                .groupId(groupInfo.getGroupId())
                .userId(user.getUserId())
                .build();
        GroupUser groupUser = GroupUser.builder()
                .groupUserId(groupUserId)
                .groupInfo(groupInfo)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .status(InvitationStatus.SUCCESS)
                .build();
        groupUserRepository.save(groupUser);
        if (!request.getEmailList().isEmpty()) {
            for (String email : request.getEmailList()) {
                iGroupUser.inviteUserToGroup(email, groupInfo.getGroupId());
            }
        }
        return groupInfoMapper.toGroupInfoResponse(groupInfo);
    }

    @Override
    public GroupInfoResponse updateGroup(Long groupId, GroupDTO groupDTO) {
        User user = iUserService.getCurrentUser();

        GroupInfo groupInfo = findGroupInfo(groupId);
        if (groupInfo != null) {
            if (groupInfo.getUser().getUserId() == user.getUserId()) {
                groupInfo.setGroupName(groupDTO.getGroupName());
                groupInfo.setNumberOfMember(groupDTO.getNumberOfMember());
                groupInfo.setDescriptions(groupDTO.getDescriptions());
                groupInfo.setCurrency(groupDTO.getCurrency());
                groupInfo.setCategory(groupDTO.getCategory());
                groupInfo.setUpdatedAt(LocalDateTime.now());
                groupInfoRepository.save((groupInfo));
            } else {
                throw new IllegalArgumentException("User is not a leader");
            }
        }
        return groupInfoMapper.toGroupInfoResponse(groupInfo);
    }

    // verify delete group condition
    @Override
    public void deleteGroup(Long groupId) {
        User user = iUserService.getCurrentUser();
        iGroupUser.deleteGroupUser(groupId);
        GroupInfo groupInfo = findGroupInfo(groupId);
        if (groupInfo != null && groupInfo.getUser().getUserId() == user.getUserId()) {
            groupInfo.setUser(null);
            groupInfoRepository.save(groupInfo);
            groupInfoRepository.deleteById(groupId);
        } else {
            throw new EntityNotFoundException("Group not found!");
        }
    }

    @Override
    public Optional<GroupInfoResponse> findGroupInfoResponse(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Id invalid");
        }

        return groupInfoRepository.findById(groupId)
                .map(groupInfoMapper::toGroupInfoResponse);

    }

    @Override
    public GroupInfo findGroupInfo(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Id invalid");
        }
        return groupInfoRepository.findById(groupId)
                .orElse(null);

    }

    @Override
    public User findLeader(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Id invalid");
        }
        return groupInfoRepository.findById(groupId)
                .orElseThrow(null).getUser();

    }

    @Override
    public void assignLeader(Long groupId, Integer userId) {
        GroupInfo groupInfo = findGroupInfo(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        groupInfo.setUser(user);
        groupInfoRepository.save(groupInfo);
    }

    @Transactional
    @Override
    public void handleQuitGroup(Long groupId) {
        User user = iUserService.getCurrentUser();
        GroupUserId groupUserId = GroupUserId.builder()
                .groupId(groupId)
                .userId(user.getUserId())
                .build();
        GroupUser groupUser = groupUserRepository.findById(groupUserId)
                .orElseThrow(() -> new EntityNotFoundException("User was not in this group"));
        if (user == this.findLeader(groupId)) {
            throw new IllegalStateException("Leader must transfer ownership before leaving the group");
        }
        if (paymentRequestRepository
                .existsByGroupInfo_GroupIdAndUser_UserIdAndStatusNot(groupId, user.getUserId(),
                        PaymentRequestStatus.SUCCESS)
                && userDebtRepository
                        .existsByGroupInfo_GroupIdAndStatusAndCreditor_UserIdOrGroupInfo_GroupIdAndStatusAndDebtor_UserId(
                                groupId, false, user.getUserId(), groupId, false, user.getUserId())) {
            groupUser.setStatus(InvitationStatus.FAILED);
        } else {
            throw new IllegalStateException("Not enough conditions to quit group");
        }
    }


}
