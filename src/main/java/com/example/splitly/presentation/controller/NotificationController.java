package com.example.splitly.presentation.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.splitly.application.service.NotificationService;
import com.example.splitly.presentation.dto.request.NotificationMessageRequest;
import com.example.splitly.presentation.dto.response.ResponseData;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/test")
  public ResponseEntity<ResponseData<?>> test(@RequestBody NotificationMessageRequest notificationMessageRequest) {
    notificationService.sendNotification(notificationMessageRequest);
    return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "test device token succesfully"));
  }

  @GetMapping
  public ResponseEntity<ResponseData<?>> getAllNotificationsByUserId() {
    return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Get notifications succesfully",
        notificationService.getAllNotifications()));
  }
}
