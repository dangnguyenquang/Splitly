package com.example.splitly.application.serviceInterface;

import com.example.splitly.domain.entity.User;
import com.example.splitly.presentation.dto.request.PermissionRequest;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.UserResponse;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    public List<UserResponse> getAll();

    public UserResponse getUserById(Integer userId);

    public User getUserEntityById(Integer userId);

    public UserResponse updateUser(Integer userId, UserRequest request);

    public UserResponse getMyInfo();

    public void handleInvitation(Long groupId, boolean action);

    public boolean checkEmail(String email);

    public List<User> findAllUserByUserIds(Set<Integer> userIds);

    public User getCurrentUser();

    List<UserResponse> searchUsersByEmail(String keyword);

    public String uploadUserAvatar(MultipartFile file, String folderName);
    public UserResponse getUserByEmail(String email);
}
