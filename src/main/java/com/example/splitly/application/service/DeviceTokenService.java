package com.example.splitly.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.DeviceToken;
import com.example.splitly.domain.repository.DeviceTokenRepository;
import com.example.splitly.presentation.dto.request.RegisterDeviceTokenRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

  private final DeviceTokenRepository deviceTokenRepository;
  private final IUserService iUserService;

  @Transactional
  public void register(RegisterDeviceTokenRequest req) {
    Integer userId = iUserService.getCurrentUser().getUserId();
    LocalDateTime now = LocalDateTime.now();

    DeviceToken deviceToken = deviceTokenRepository.findByUserIdAndDeviceId(userId, req.getDeviceId())
        .orElseGet(DeviceToken::new);

    deviceToken.setUserId(userId);
    deviceToken.setDeviceId(req.getDeviceId());
    deviceToken.setToken(req.getToken());
    deviceToken.setPlatform(req.getPlatform() == null ? "ANDROID" : req.getPlatform());
    deviceToken.setActive(true);
    deviceToken.setLastSeenAt(now);
    deviceToken.setUpdatedAt(now);

    deviceTokenRepository.save(deviceToken);
  }
}
