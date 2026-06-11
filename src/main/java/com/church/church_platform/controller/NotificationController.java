package com.church.church_platform.controller;

import com.church.church_platform.service
        .NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService
            notificationService;

    // GET /api/notifications
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>>
    getNotifications() {
        return ResponseEntity.ok(
                notificationService.getMyNotifications()
        );
    }

    // GET /api/notifications/count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>>
    getUnreadCount() {
        return ResponseEntity.ok(
                Map.of("count",
                        notificationService.getUnreadCount()
                )
        );
    }

    // PUT /api/notifications/read-all
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(
                Map.of("message", "All marked as read!")
        );
    }

    // PUT /api/notifications/{id}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                Map.of("message", "Marked as read!")
        );
    }
}