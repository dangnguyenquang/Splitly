package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.InvitationStatus;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.domain.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupUserImpl implements IGroupUser {

    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;

    @Override
    public void inviteUserToGroup(String email, Long groupId) {
        Optional<User> optional = userRepository.findByEmail(email);
        if (optional.isPresent()) {
            User user = optional.get();
            GroupUserId groupUserId = GroupUserId.builder()
                    .userId(user.getUserId())
                    .groupId(groupId)
                    .build();
            Optional<GroupUser> optionalGU = groupUserRepository.findById(groupUserId);

            if (optionalGU.isPresent()) {
                GroupUser existing = optionalGU.get();

                if (existing.getStatus() == InvitationStatus.FAILED) {
                    existing.setStatus(InvitationStatus.WAITING);
                    existing.setJoinedAt(LocalDateTime.now());
                    groupUserRepository.save(existing);
                } else if (existing.getStatus() == InvitationStatus.SUCCESS) {
                    throw new EntityExistsException("User existed in group!");
                } else {
                    throw new EntityExistsException("Invitation has been sent!");
                }

            } else {
                GroupUser groupUser = GroupUser.builder()
                        .groupUserId(groupUserId)
                        .status(InvitationStatus.WAITING)
                        .joinedAt(LocalDateTime.now())
                        .build();
                groupUserRepository.save(groupUser);
            }
        } else {
            throw new EntityNotFoundException("Not found user with " + email + "!");
        }
    }

    @Override
    public void removeUserOutGroup(Integer userId, Long groupId, Integer leaderId) {
        GroupUserId groupUserId = GroupUserId.builder()
                .userId(userId)
                .groupId(groupId)
                .build();
        Optional<GroupUser> optional = groupUserRepository.findById(groupUserId);
        if (Objects.equals(userId, leaderId)) {
            throw new EntityExistsException("Can not remove leader");
        }
        if (optional.isPresent()) {
            GroupUser existing = optional.get();
            GroupInfo groupInfo = existing.getGroupInfo();
            if (existing.getStatus() != InvitationStatus.SUCCESS) {
                throw new EntityNotFoundException("Not found user in group!");
            }
            if (groupInfo.getUser().getUserId() == leaderId) {
                existing.setStatus(InvitationStatus.FAILED);
                groupUserRepository.save(existing);
            } else {
                throw new EntityNotFoundException("User is not a leader!");
            }

        } else {
            throw new EntityNotFoundException("Not found user in group!");
        }
    }

    @Override
    public List<User> getAllUserGroup(Long groupId) {
        List<User> users = groupUserRepository.findUsersByGroupId(groupId);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Not found group!");
        }
        return users;
    }

    @Override
    public List<GroupInfo> getGroupsByUserId(Integer userId) {
        List<GroupInfo> groups = groupUserRepository.findAllGroupsByUserId(userId);
        if (!groups.isEmpty()) {
            return groups;
        } else {
            throw new EntityNotFoundException("Not found groups");
        }
    }

    @Transactional
    @Override
    public void deleteGroupUser(Long groupId) {
        try {
            groupUserRepository.deleteGroupUserById(groupId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Group with ID " + groupId + " not found");
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete group user", e);
        }
    }

    @Override
    public boolean areUsersInGroup(Long groupId, List<Integer> userIds) {
        List<Integer> existingUserIds = groupUserRepository.findExistingSuccessfulUsersInGroup(groupId, userIds);
        return new HashSet<>(existingUserIds).containsAll(userIds);
    }
}
