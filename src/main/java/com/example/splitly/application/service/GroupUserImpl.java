package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.service.IGroupUser;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupUserImpl implements IGroupUser {

    private final GroupUserRepository groupUserRepository;

    @Override
    public void inviteUserToGroup(Integer userId, Long groupId) {
        GroupUserId groupUserId = GroupUserId.builder()
                .userId(userId)
                .groupId(groupId)
                .build();
        Optional<GroupUser> optional = groupUserRepository.findById(groupUserId);

        if (optional.isPresent()) {
            GroupUser existing = optional.get();

            if (!existing.getStatus()) {
                existing.setStatus(true);
                existing.setJohnAt(LocalDateTime.now());
                groupUserRepository.save(existing);
            }

        } else {
            GroupUser groupUser = GroupUser.builder()
                    .groupUserId(groupUserId)
                    .status(true)
                    .johnAt(LocalDateTime.now())
                    .build();
            groupUserRepository.save(groupUser);
        }
    }

    @Override
    public void removeUserOutGroup(Integer userId, Long groupId, Integer leaderId) {
        GroupUserId groupUserId = GroupUserId.builder()
                .userId(userId)
                .groupId(groupId)
                .build();
        Optional<GroupUser> optional = groupUserRepository.findById(groupUserId);
        if (optional.isPresent()) {
            GroupUser existing = optional.get();
            GroupInfo groupInfo = existing.getGroupInfo();

            if (existing.getStatus() && groupInfo.getUser().getUserId() == leaderId) {
                existing.setStatus(false);
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

}
