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
    private final PaymentRequestService paymentRequestService;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public BillOcrResponse processBillImage(BillOcrRequest request) {
        // Get current user for audit logging
        var currentUser = userService.getCurrentUser();

        MultipartFile image = request.getImage();

        // Validate image
        validateImage(image);

        log.info("User {} processing bill image: {} (size: {} bytes)",
                currentUser.getUserId(), image.getOriginalFilename(), image.getSize());

        // Process with Gemini
        BillOcrResponse response = geminiService.extractBillInformation(
                image,
                request.getAdditionalContext()
        );

        paymentRequestService.uploadPaymentRequestImage(request.getImage(), request.getPaymentId());

        log.info("Bill processing completed for user {} with confidence: {}%",
                currentUser.getUserId(), response.getConfidence());

        return response;
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image file is too large. Maximum size is 10MB");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid image format. Allowed formats: JPEG, PNG, WebP, HEIC"
            );
        }
    }
}
