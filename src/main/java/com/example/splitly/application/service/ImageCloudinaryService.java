package com.example.splitly.application.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.splitly.application.serviceInterface.IGroupInfo;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.GroupInfoRepository;
import com.example.splitly.domain.repository.UserRepository;
import com.google.api.gax.rpc.UnauthenticatedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageCloudinaryService {
    public final Cloudinary cloudinary;
    public final IGroupInfo iGroupInfo;
    public final GroupInfoRepository groupInfoRepository;
    public final IUserService iUserService;
    public final UserRepository userRepository;

    public Map<String, Object> uploadImageFile(MultipartFile files, String folder, @Nullable String publicId) {
        System.out.println("Folder = " + folder);
        if (files.isEmpty())
            throw new IllegalArgumentException("File is empty");

        String contentType = files.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new IllegalArgumentException("File is not an image");

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("folder", folder);
            params.put("overwrite", true);
            params.put("invalidate", true);
            params.put("resource_type", "image");
            if (publicId != null && !publicId.isBlank()) {
                params.put("public_id", publicId);

                params.put("use_filename", false);
                params.put("unique_filename", false);
            } else {
                params.put("use_filename", false);
                params.put("unique_filename", true);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(files.getBytes(), params);
            System.out.println("secure_url = " + uploadResult.get("secure_url"));

            return uploadResult;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image file", e);
        }
    }

    public String deleteImage(String publicId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return (String) result.get("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to delete image", e);
        }
    }

    public void uploadGroupImage(MultipartFile file, Long groupId, String folderName) {
        User user = iUserService.getCurrentUser();
        if (user == null)
            throw new AccessDeniedException("Not logged in.");
        if (!folderName.equals("groups")) {
            throw new IllegalArgumentException("Invalid folder name: " + folderName);
        }
        GroupInfo groupInfo = iGroupInfo.findGroupInfo(groupId);
        String folder = folderName + "/" + groupId;
        String existingPublicId = groupInfo.getImagePublicId();

        Map<String, Object> uploadResult = uploadImageFile(file, folder, existingPublicId);
        groupInfo.setGroupImage((String) uploadResult.get("secure_url"));
        groupInfo.setImagePublicId((String) uploadResult.get("public_id"));
        groupInfoRepository.save(groupInfo);
    }

    public void uploadUserAvatar(MultipartFile file, String folderName) {
        User user = iUserService.getCurrentUser();
        if (user == null)
            throw new AccessDeniedException("Not logged in.");

        if (!folderName.equals("users")) {
            throw new IllegalArgumentException("Invalid folder name: " + folderName);
        }

        String folder = folderName + "/" + user.getUserId();

        String existingPublicId = user.getImagePublicId();

        Map<String, Object> uploadResult = uploadImageFile(file, folder, existingPublicId);

        user.setUserImage((String) uploadResult.get("secure_url"));
        user.setImagePublicId((String) uploadResult.get("public_id"));
        userRepository.save(user);

    }

}
