package com.example.splitly.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.splitly.application.service.ImageCloudinaryService;
import com.example.splitly.presentation.dto.response.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageCloudinaryController {

    public final ImageCloudinaryService iCloudinaryService;

    @PostMapping({ "/upload-avatar/{folderName}/{groupId}" })
    public ResponseEntity<ResponseData<?>> uploadGroupAvatar(
            @PathVariable String folderName,
            @PathVariable(required = true) Long groupId,
            @RequestParam("file") MultipartFile file) {
        iCloudinaryService.uploadGroupImage(file, groupId, folderName);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Upload successfully!");
        return ResponseEntity.ok(responseData);
    }

    @PostMapping({ "/upload-avatar/{folderName}" })
    public ResponseEntity<ResponseData<?>> uploadAvatarUser(
            @PathVariable String folderName,
            @RequestParam("file") MultipartFile file) {
        iCloudinaryService.uploadUserAvatar(file, folderName);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Upload successfully!");
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/remove/{folderName}/{publicId}")
    public ResponseEntity<ResponseData<?>> removeImage(@PathVariable String folderName, @PathVariable String publicId) {
        iCloudinaryService.deleteImage(publicId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Delete image successfully");
        return ResponseEntity.ok(responseData);
    }
}
