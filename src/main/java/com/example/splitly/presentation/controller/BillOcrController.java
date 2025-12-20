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

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillOcrController {

    private final BillOcrService billOcrService;

    /**
     * Process bill image and extract information using Gemini AI
     *
     * @param image             Bill/receipt image file
     * @param additionalContext Optional context (e.g., restaurant name, date)
     * @return Extracted bill information in JSON format
     */
    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<BillOcrResponse> processBillImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("paymentId") Integer paymentId,
            @RequestParam(value = "additionalContext", required = false) String additionalContext) {

        BillOcrRequest request = new BillOcrRequest();
        request.setImage(image);
        request.setAdditionalContext(additionalContext);
        request.setPaymentId(paymentId);

        BillOcrResponse response = billOcrService.processBillImage(request);

        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Bill processed successfully",
                response
        );
    }
}