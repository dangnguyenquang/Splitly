package com.example.splitly.application.service;

import com.example.splitly.presentation.dto.request.BillOcrRequest;
import com.example.splitly.presentation.dto.response.BillOcrResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillOcrService {

    private final GeminiService geminiService;
    private final UserService userService;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB per file
    private static final int MAX_FILES_PER_REQUEST = 10; // Max images for one bill

    /**
     * Process single or multiple images of the SAME bill
     * Returns ONE combined bill response
     */
    public BillOcrResponse processBillImage(BillOcrRequest request) {
        var currentUser = userService.getCurrentUser();
        List<MultipartFile> images = request.getImages();

        // Validate all images
        validateBatchRequest(images);

        for (MultipartFile image : images) {
            validateImage(image);
        }

        log.info("User {} processing {} images of the same bill (total size: {} KB)",
                currentUser.getUserId(),
                images.size(),
                images.stream().mapToLong(MultipartFile::getSize).sum() / 1024);

        // Process all images together to get ONE bill
        BillOcrResponse response = geminiService.extractBillInformationFromMultipleImages(
                images,
                request.getAdditionalContext()
        );

        log.info("Bill processing completed for user {} with confidence: {}% ({} items found)",
                currentUser.getUserId(),
                response.getConfidence(),
                response.getItems() != null ? response.getItems().size() : 0);

        return response;
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Image file is too large. Maximum size is 10MB. File: " + image.getOriginalFilename()
            );
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid image format for file: " + image.getOriginalFilename() +
                            ". Allowed formats: JPEG, PNG, WebP, HEIC"
            );
        }
    }

    private void validateBatchRequest(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }

        if (images.size() > MAX_FILES_PER_REQUEST) {
            throw new IllegalArgumentException(
                    "Maximum " + MAX_FILES_PER_REQUEST + " images allowed per request. Received: " + images.size()
            );
        }

        // Calculate total size
        long totalSize = images.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        long maxTotalSize = (long) MAX_FILE_SIZE * MAX_FILES_PER_REQUEST;
        if (totalSize > maxTotalSize) {
            throw new IllegalArgumentException(
                    "Total file size exceeds limit. Maximum: " + (maxTotalSize / 1024 / 1024) + "MB"
            );
        }
    }
}

