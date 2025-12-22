package com.example.splitly.application.service;

import com.example.splitly.application.mapper.UserMapper;
import com.example.splitly.application.serviceInterface.IImageCloudinaryService;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.Role;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.enumerator.InvitationStatus;
import com.example.splitly.domain.repository.GroupUserRepository;
import com.example.splitly.domain.repository.RoleRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final IImageCloudinaryService iImageCloudinaryService;

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
        if (request.getRoles() != null) {
            Set<Integer> roleIds = request.getRoles().stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toSet());
    
            List<Role> roles = roleRepository.findAllById(roleIds);
    
            existingUser.setRoles(new HashSet<>(roles));
        }
        existingUser.setFullName(request.getFullName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhone(request.getPhone());

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
    public List<UserResponse> searchUsersByEmail(String keyword) {
        User currentUser = getCurrentUser();

        // Validate keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuntimeException("Search keyword cannot be empty");
        }

        String trimmedKeyword = keyword.trim();

        log.info("User {} searching for users with email keyword: {}",
                currentUser.getUserId(), trimmedKeyword);

        List<User> users = userRepository.searchByEmailKeyword(trimmedKeyword, currentUser.getUserId());

        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }


    public List<UserResponse> searchUsers(String keyword) {
        User currentUser = getCurrentUser();

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuntimeException("Search keyword cannot be empty");
        }

        String trimmedKeyword = keyword.trim();

        log.info("User {} searching for users with keyword: {}",
                currentUser.getUserId(), trimmedKeyword);

        List<User> users = userRepository.searchByEmailOrUsernameKeyword(
                trimmedKeyword,
                currentUser.getUserId()
        );

        log.info("Found {} users matching keyword: {}", users.size(), trimmedKeyword);

        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

@Override
        public String uploadUserAvatar(MultipartFile file, String folderName) {
        User user = getCurrentUser();
        if (user == null)
            throw new AccessDeniedException("Not logged in.");

        if (!folderName.equals("users")) {
            throw new IllegalArgumentException("Invalid folder name: " + folderName);
        }

        String folder = folderName + "/" + user.getUserId();

        String existingPublicId = user.getImagePublicId();

        Map<String, Object> uploadResult = iImageCloudinaryService.uploadImageFile(file, folder, existingPublicId);

        user.setUserImage((String) uploadResult.get("secure_url"));
        user.setImagePublicId((String) uploadResult.get("public_id"));
        userRepository.save(user);

        return user.getUserImage();

    }

}
