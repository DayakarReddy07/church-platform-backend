package com.church.church_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who receives this notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Notification type
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // Message to show
    private String message;

    // Link to navigate to
    private String link;

    // Image to show (church logo, profile pic)
    private String imageUrl;

    // Is it read?
    private boolean isRead = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        NEW_FOLLOWER,    // Someone followed church
        NEW_SERMON,      // Church uploaded sermon
        NEW_EVENT,       // Church created event
        NEW_POST,        // Church made a post
        EVENT_REMINDER,  // Event is tomorrow
        PRAYER_RESPONSE  // Someone prayed for you
    }
}