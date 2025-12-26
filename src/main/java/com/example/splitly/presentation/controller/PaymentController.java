package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.domain.enumerator.PaymentImageType;
import com.example.splitly.presentation.dto.request.PaymentRequest;
import com.example.splitly.presentation.dto.request.UpdateStatusProcessPaymentRequest;
import com.example.splitly.presentation.dto.request.UpdateStatusSuccessPaymentRequest;
import com.example.splitly.presentation.dto.response.PaymentImageResponse;
import com.example.splitly.presentation.dto.response.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/payment-request")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final IPaymentRequestService paymentRequestService;

    @PostMapping("/{groupId}")
    public ResponseData<?> create(@RequestBody PaymentRequest request, @PathVariable Long groupId) {
        var response = paymentRequestService.create(request, groupId);

        return new ResponseData<>(HttpStatus.OK.value(), "Create payment request successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getById(@PathVariable Integer id) {
        var response = paymentRequestService.getById(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Get payment request by id successfully", response);
    }

    @GetMapping()
    public ResponseData<?> getAllByUserId() {
        var response = paymentRequestService.getAllPaymentRequestByUserId();

        return new ResponseData<>(HttpStatus.OK.value(), "Get all payments successfully", response);
    }

    @GetMapping("/consensus")
    public ResponseData<?> getAllByConsensusUserId() {
        var response = paymentRequestService.getAllPaymentRequestByConsensusUserId();

        return new ResponseData<>(HttpStatus.OK.value(), "Get all payments by consensus successfully", response);
    }

    @GetMapping("/{id}/group")
    public ResponseData<?> getAllByGroupId(@PathVariable Long id) {
        var response = paymentRequestService.getAllPaymentRequestByGroupId(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Get all payments by group successfully", response);
    }

    @PutMapping("/{id}")
    public ResponseData<?> updatePaymentRequest(@RequestBody PaymentRequest paymentRequest, @PathVariable Integer id) {
        var response = paymentRequestService.updatePaymentRequest(paymentRequest, id);

        return new ResponseData<>(HttpStatus.OK.value(), "Update payment successfully", response);
    }

    @PutMapping("/{id}/fail")
    public ResponseData<?> changeStatusPaymentRequestToFailed(@PathVariable Integer id) {
        var response = paymentRequestService.changeStatusPaymentRequestToFailed(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Change status payment to failed successfully", response);
    }

//    @PutMapping("/{id}/processing")
//    public ResponseData<?> changeStatusPaymentRequestToProcessing(@PathVariable Integer id) {
//        var response = paymentRequestService.changeStatusPaymentRequestToProcessing(id);
//
//        return new ResponseData<>(HttpStatus.OK.value(), "Change status payment to processing successfully", response);
//    }

//    @PutMapping("/{id}/awaiting-confirmation")
//    public ResponseData<?> changeStatusPaymentRequestToAwaitingConfirmation(@RequestBody PaymentRequest paymentRequest, @PathVariable Integer id) {
//        var response = paymentRequestService.changeStatusPaymentRequestToAwaitingConfirmation(id, paymentRequest);
//
//        return new ResponseData<>(HttpStatus.OK.value(), "Change status payment to awaiting confirmation successfully", response);
//    }

    @PutMapping("/{id}/ready-to-split")
    public ResponseData<?> changeStatusPaymentRequestToReadyToSpilt(@PathVariable Integer id) {
        var response = paymentRequestService.changeStatusPaymentRequestToSplit(id);

        return new ResponseData<>(HttpStatus.OK.value(), "Change status payment to ready to split successfully", response);
    }

//    @PutMapping("/{id}/consensus/process")
//    public ResponseData<?> updateConsensusStatusOfProcess(@PathVariable Integer id, @RequestBody UpdateStatusProcessPaymentRequest updateStatusProcessPaymentRequest) {
//        var response = paymentRequestService.updateProcessStatusOfConsensus(id, updateStatusProcessPaymentRequest);
//
//        return new ResponseData<>(HttpStatus.OK.value(), "Updated consensus payment request", response);
//    }

    @PutMapping("/{id}/consensus/success")
    public ResponseData<?> updateConsensusStatusOfSuccess(@PathVariable Integer id, @RequestBody UpdateStatusSuccessPaymentRequest updateStatusSuccessPaymentRequest) {
        var response = paymentRequestService.updateSuccessStatusOfConsensus(id, updateStatusSuccessPaymentRequest);

        return new ResponseData<>(HttpStatus.OK.value(), "Updated consensus payment request", response);
    }

    @PostMapping("/payments/{id}/images")
    public ResponseEntity<ResponseData<List<PaymentImageResponse>>> uploadPaymentImages(
            @PathVariable Integer id,
            @RequestParam("images") List<MultipartFile> files,
            @RequestParam PaymentImageType type
    ) {
        var responses =
                paymentRequestService.uploadPaymentRequestImages(files, id, type);

        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(), "Upload successfully!", responses)
        );
    }
}
