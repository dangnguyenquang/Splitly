package com.example.splitly.presentation.controller;

import com.example.splitly.application.service.BillOcrService;
import com.example.splitly.presentation.dto.request.BillOcrRequest;
import com.example.splitly.presentation.dto.response.BillOcrResponse;
import com.example.splitly.presentation.dto.response.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillOcrController {

    private final BillOcrService billOcrService;

    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<BillOcrResponse> processBillImage(
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(value = "additionalContext", required = false) String additionalContext) {

        BillOcrRequest request = new BillOcrRequest();
        request.setImages(images);
        request.setAdditionalContext(additionalContext);

        BillOcrResponse response = billOcrService.processBillImage(request);

        String message = images.size() == 1
                ? "Bill processed successfully"
                : String.format("Bill processed successfully from %d images", images.size());

        return new ResponseData<>(
                HttpStatus.OK.value(),
                message,
                response
        );
    }
}