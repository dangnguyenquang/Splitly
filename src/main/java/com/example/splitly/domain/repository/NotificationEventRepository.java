package com.example.splitly.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.splitly.domain.entity.NotificationEvent;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    
}
