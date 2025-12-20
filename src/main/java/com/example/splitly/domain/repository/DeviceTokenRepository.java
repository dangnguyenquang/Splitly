package com.example.splitly.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.splitly.domain.entity.DeviceToken;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserIdAndDeviceId(Integer userId, String deviceId);

    List<DeviceToken> findByUserIdInAndActiveTrue(List<Integer> userIds);

    Optional<DeviceToken> findByToken(String token);
}
