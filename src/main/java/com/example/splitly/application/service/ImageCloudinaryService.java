package com.example.splitly.application.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.splitly.application.serviceInterface.IImageCloudinaryService;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageCloudinaryService implements IImageCloudinaryService {
    public final Cloudinary cloudinary;


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

}
