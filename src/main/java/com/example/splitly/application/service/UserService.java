package com.example.splitly.application.service;

import com.example.splitly.application.mapper.UserMapper;
import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.application.serviceInterface.IGroupInfo;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.Role;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.InvitationStatus;
import com.example.splitly.domain.enumerator.PaymentRequestStatus;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.domain.repository.PaymentRequestRepository;
import com.example.splitly.domain.repository.RoleRepository;
import com.example.splitly.domain.repository.UserDebtRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.PaymentResponse;
import com.example.splitly.presentation.dto.response.PaymentUserResponse;
import com.example.splitly.presentation.dto.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final GroupUserRepository groupUserRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final UserDebtRepository userDebtRepository;
    private final IGroupInfo iGroupInfo;

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in DB")));
    }

    @Override
    public User getUserEntityById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in DB"));
    }

    @Override
    public UserResponse updateUser(Integer userId, UserRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in DB"));

        userMapper.updateUser(existingUser, request);
        Set<Integer> roleIds = request.getRoles().stream()
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        List<Role> roles = roleRepository.findAllById(roleIds);

        existingUser.setFullName(request.getFullName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhone(request.getPhone());
        existingUser.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(existingUser));
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByEmail(name).orElseThrow(() -> new ApplicationContextException("")); // This
                                                                                                             // code
                                                                                                             // will be
                                                                                                             // update
                                                                                                             // soon
                                                                                                             // when we
                                                                                                             // have
                                                                                                             // application
                                                                                                             // exception
                                                                                                             // code

        return userMapper.toUserResponse(user);
    }

    @Override
    public void handleInvitation(Long groupId, boolean action) {
        User user = getCurrentUser();
        GroupUserId groupUserId = GroupUserId.builder()
                .groupId(groupId)
                .userId(user.getUserId())
                .build();
        Optional<GroupUser> optional = groupUserRepository.findById(groupUserId);
        if (optional.isPresent()) {
            GroupUser groupUser = optional.get();
            if (action && groupUser.getStatus() == InvitationStatus.WAITING) {
                groupUser.setStatus(InvitationStatus.SUCCESS);
                groupUser.setJoinedAt(LocalDateTime.now());
            } else if (!action && groupUser.getStatus() == InvitationStatus.WAITING) {
                groupUser.setStatus(InvitationStatus.FAILED);
            }
            groupUserRepository.save(groupUser);
        }
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found in DB"));
    }

    @Override
    public List<User> findAllUserByUserIds(Set<Integer> userIds) {
        return userRepository.findAllById(userIds);
    }

    @Override
    @Transactional
    public void handleQuitGroup(Long groupId) {
        User user = this.getCurrentUser();
        GroupUserId groupUserId = GroupUserId.builder()
                .groupId(groupId)
                .userId(user.getUserId())
                .build();
        GroupUser groupUser = groupUserRepository.findById(groupUserId)
                .orElseThrow(() -> new EntityNotFoundException("User was not in this group"));
        if (user == iGroupInfo.findLeader(groupId)) {
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
