package com.example.splitly.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.splitly.domain.entity.NotificationRecipient;
import com.example.splitly.presentation.dto.response.UserNotificationResponse;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Integer> {
    @Modifying
    @Query("""
        select new com.example.splitly.presentation.dto.response.UserNotificationResponse (
            r.notificationRecipientsId,
            u.userId,
            r.readAt,
            r.eventStatus,
            
            e.user.userId,
            e.eventId,
            e.title,
            e.body,
            e.notificationImage,
            e.payload,
            e.notificationType,
            e.createdAt
        )
        from NotificationRecipient r
        join r.user u
        join r.notificationEvent e
        where u.userId = :userId
        order by r.createdAt desc
    """)
    List<UserNotificationResponse> findAllByRecipientUserId(@Param("userId") Integer userId);

}
