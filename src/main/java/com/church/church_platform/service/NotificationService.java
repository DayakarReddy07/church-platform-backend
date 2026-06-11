package com.church.church_platform.service;

import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository
            notificationRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // ─── Helper: Get current user ──────────
    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // Get my notifications
    public List<Map<String, Object>>
    getMyNotifications() {

        User user = getCurrentUser();
        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(20) // max 20 notifications
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Get unread count
    public Long getUnreadCount() {
        User user = getCurrentUser();
        return notificationRepository
                .countByUserAndIsReadFalse(user);
    }

    // Mark all as read
    public void markAllAsRead() {
        User user = getCurrentUser();
        notificationRepository.markAllAsRead(user);
    }

    //  Mark one as read
    public void markAsRead(Long notifId) {
        Notification notif = notificationRepository
                .findById(notifId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notification not found!"
                        )
                );
        notif.setRead(true);
        notificationRepository.save(notif);
    }

    // Create notification for all
    // followers when church posts sermon
    public void notifyFollowersNewSermon(
            Church church,
            String sermonTitle) {

        List<Follow> followers =
                followRepository.findByChurch(church);

        followers.forEach(follow -> {
            Notification notif = Notification.builder()
                    .user(follow.getUser())
                    .type(Notification.NotificationType
                            .NEW_SERMON)
                    .message(church.getName() +
                            " uploaded a new sermon: \"" +
                            sermonTitle + "\"")
                    .link("/app/feed")
                    .imageUrl(church.getLogo())
                    .build();

            notificationRepository.save(notif);
        });
    }

    //  Notify followers of new event
    public void notifyFollowersNewEvent(
            Church church,
            String eventTitle) {

        List<Follow> followers =
                followRepository.findByChurch(church);

        followers.forEach(follow -> {
            Notification notif = Notification.builder()
                    .user(follow.getUser())
                    .type(Notification.NotificationType
                            .NEW_EVENT)
                    .message(church.getName() +
                            " created a new event: \"" +
                            eventTitle + "\"")
                    .link("/app/events")
                    .imageUrl(church.getLogo())
                    .build();

            notificationRepository.save(notif);
        });
    }

    // Notify followers of new post
    public void notifyFollowersNewPost(
            Church church,
            String postTitle) {

        List<Follow> followers =
                followRepository.findByChurch(church);

        followers.forEach(follow -> {
            Notification notif = Notification.builder()
                    .user(follow.getUser())
                    .type(Notification.NotificationType
                            .NEW_POST)
                    .message(church.getName() +
                            " posted: \"" +
                            (postTitle != null ?
                                    postTitle : "New post") +
                            "\"")
                    .link("/app/feed")
                    .imageUrl(church.getLogo())
                    .build();

            notificationRepository.save(notif);
        });
    }

    //  Notify church admin of new follower
    public void notifyNewFollower(
            Church church,
            User follower) {

        Notification notif = Notification.builder()
                .user(church.getAdmin())
                .type(Notification.NotificationType
                        .NEW_FOLLOWER)
                .message(follower.getName() +
                        " started following " +
                        church.getName())
                .link("/app/admin")
                .imageUrl(follower.getProfilePic())
                .build();

        notificationRepository.save(notif);
    }

    // Map notification to response
    private Map<String, Object> mapToResponse(
            Notification notif) {

        Map<String, Object> map =
                new java.util.HashMap<>();
        map.put("id", notif.getId());
        map.put("type", notif.getType().name());
        map.put("message", notif.getMessage());
        map.put("link", notif.getLink());
        map.put("imageUrl", notif.getImageUrl());
        map.put("isRead", notif.isRead());
        map.put("createdAt", notif.getCreatedAt());
        return map;
    }
}