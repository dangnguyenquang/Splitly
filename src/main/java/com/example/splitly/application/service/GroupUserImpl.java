package com.example.splitly.application.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.splitly.domain.repository.UserConnectionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.splitly.application.mapper.GroupInfoMapper;
import com.example.splitly.application.mapper.UserMapper;
import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.InvitationStatus;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import com.example.splitly.presentation.dto.response.UserResponse;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static com.example.splitly.util.PiiMasker.maskEmail;

@Service
@RequiredArgsConstructor
public class GroupUserImpl implements IGroupUser {

    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final IUserService iUserService;
    private final UserMapper userMapper;
    private final GroupInfoMapper groupInfoMapper;
    private final UserConnectionRepository userConnectionRepository;

    @Override
    public void inviteUserToGroup(String email, Long groupId) {
        User currentUser = iUserService.getCurrentUser();

        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Not found user with " + maskEmail(email) + "!");
        }

        User targetUser = optional.get();

        // Prevent inviting yourself
        if (targetUser.getUserId() == (currentUser.getUserId())) {
            throw new IllegalArgumentException("You cannot invite yourself to a group!");
        }

        // Check if users have an accepted connection
        boolean areConnected = userConnectionRepository.areUsersConnected(
                currentUser.getUserId(),
                targetUser.getUserId()
        );

        if (!areConnected) {
            throw new IllegalStateException(
                    "You can only invite users you're connected with. " +
                            "Please send a connection request to " + maskEmail(email) + " first."
            );
        }

        // Check if current user has permission to invite (is member/admin of the group)
        GroupUserId currentUserGroupId = GroupUserId.builder()
                .userId(currentUser.getUserId())
                .groupId(groupId)
                .build();

        Optional<GroupUser> currentUserInGroup = groupUserRepository.findById(currentUserGroupId);

        if (currentUserInGroup.isEmpty() ||
                currentUserInGroup.get().getStatus() != InvitationStatus.SUCCESS) {
            throw new IllegalStateException("You must be a member of this group to invite others!");
        }

        GroupUserId groupUserId = GroupUserId.builder()
                .userId(targetUser.getUserId())
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
                throw new EntityExistsException("User already exists in group!");
            } else {
                throw new EntityExistsException("Invitation has already been sent to " + maskEmail(email));
            }
        } else {
            GroupUser groupUser = GroupUser.builder()
                    .groupUserId(groupUserId)
                    .status(InvitationStatus.WAITING)
                    .joinedAt(LocalDateTime.now())
                    .build();
            groupUserRepository.save(groupUser);
        }
    }

    @Override
    public void removeUserOutGroup(Integer userId, Long groupId) {
        User user = iUserService.getCurrentUser();
        GroupUserId groupUserId = GroupUserId.builder()
                .userId(userId)
                .groupId(groupId)
                .build();
        Optional<GroupUser> optional = groupUserRepository.findById(groupUserId);
        if (Objects.equals(userId, user.getUserId())) {
            throw new EntityExistsException("Can not remove leader");
        }
        if (optional.isPresent()) {
            GroupUser existing = optional.get();
            GroupInfo groupInfo = existing.getGroupInfo();
            if (existing.getStatus() != InvitationStatus.SUCCESS) {
                throw new EntityNotFoundException("Not found user in group!");
            }
            if (groupInfo.getUser().getUserId() == user.getUserId()) {
                existing.setStatus(InvitationStatus.FAILED);
                groupUserRepository.save(existing);
            } else {
                throw new IllegalAccessError("User is not a leader!");
            }

        } else {
            throw new EntityNotFoundException("Not found user in group!");
        }
    }

    @Override
    public List<UserResponse> getAllUserGroup(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Invalid ID!");
        }
        List<User> users = groupUserRepository.findUsersByGroupId(groupId);
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public List<GroupInfoResponse> getGroupsByUserId() {
        User user = iUserService.getCurrentUser();
        List<GroupInfo> groups = groupUserRepository.findAllGroupsByUserId(user.getUserId());
        return groupInfoMapper.toGroupInfoResponses(groups);
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
