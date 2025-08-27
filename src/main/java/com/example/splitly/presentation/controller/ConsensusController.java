package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.IConsensusService;
import com.example.splitly.presentation.dto.request.ConsensusPaymentRequest;
import com.example.splitly.presentation.dto.response.ConsensusPaymentResponse;
import com.example.splitly.presentation.dto.response.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/consensus")
public class ConsensusController {
    private final IConsensusService consensusService;

    @GetMapping
    public ResponseData<?> getAll() {
        Set<ConsensusPaymentResponse> consensusPaymentResponses = consensusService.getAllConsensusByUser();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all consensus payment by user id successfully!", consensusPaymentResponses);
    }
}
