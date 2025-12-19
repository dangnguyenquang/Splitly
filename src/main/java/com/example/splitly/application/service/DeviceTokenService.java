package com.example.splitly.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.domain.entity.DeviceToken;
import com.example.splitly.domain.entity.User;
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
    var now = LocalDateTime.now();

    var entity = deviceTokenRepository.findByUserIdAndDeviceId(userId, req.getDeviceId())
        .orElseGet(DeviceToken::new);

    entity.setUserId(userId);
    entity.setDeviceId(req.getDeviceId());
    entity.setToken(req.getToken());
    entity.setPlatform(req.getPlatform() == null ? "ANDROID" : req.getPlatform());
    entity.setActive(true);
    entity.setLastSeenAt(now);
    entity.setUpdatedAt(now);

    deviceTokenRepository.save(entity);
  }
}
