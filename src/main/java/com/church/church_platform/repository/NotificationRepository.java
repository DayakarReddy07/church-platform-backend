package com.church.church_platform.repository;

import com.church.church_platform.entity.Notification;
import com.church.church_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get all notifications for user
    // newest first
    List<Notification> findByUserOrderByCreatedAtDesc(
            User user
    );

    // Get unread notifications
    List<Notification> findByUserAndIsReadFalse(
            User user
    );

    // Count unread notifications
    Long countByUserAndIsReadFalse(User user);

    // Mark all as read for user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true " +
            "WHERE n.user = :user AND n.isRead = false")
    void markAllAsRead(User user);
}