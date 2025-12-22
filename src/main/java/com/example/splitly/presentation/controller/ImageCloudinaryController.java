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

    @DeleteMapping("/remove/{folderName}/{publicId}")
    public ResponseEntity<ResponseData<?>> removeImage(@PathVariable String folderName, @PathVariable String publicId) {
        iCloudinaryService.deleteImage(publicId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Delete image successfully");
        return ResponseEntity.ok(responseData);
    }
}
