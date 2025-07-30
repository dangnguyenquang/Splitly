package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.response.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-request")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final IPaymentRequestService paymentRequestService;

    @PostMapping
    public ResponseData<?> create(@RequestBody PaymentRequest request) {
        var response = paymentRequestService.create(request);

        return new ResponseData<>(HttpStatus.OK.value(), "Create payment request successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getById(@PathVariable Integer id) {
        var response = paymentRequestService.getById(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Get payment request successfully", response);
    }

    @GetMapping()
    public ResponseData<?> getAll() {
        var response = paymentRequestService.getAllPaymentRequest();

        return new ResponseData<>(HttpStatus.OK.value(), "Get all payments successfully", response);
    }

}
