package com.example.splitly.presentation.controller;

import com.example.splitly.presentation.dto.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<ResponseData<String>> healthCheck() {
        return ResponseEntity.ok(
                new ResponseData<>(HttpStatus.OK.value(), "Service is UP and running", "OK")
        );
    }
}
