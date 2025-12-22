package com.example.splitly.application.serviceInterface;

import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.web.multipart.MultipartFile;

public interface IImageCloudinaryService {
    public Map<String, Object> uploadImageFile(MultipartFile files, String folder, @Nullable String publicId);
}
