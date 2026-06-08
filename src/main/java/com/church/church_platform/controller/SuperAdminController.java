package com.church.church_platform.controller;

import com.church.church_platform.dto.response.ChurchResponse;
import com.church.church_platform.entity.Church;
import com.church.church_platform.entity.User;
import com.church.church_platform.repository.*;
import com.church.church_platform.service.ChurchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SuperAdminController {

    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final SermonRepository sermonRepository;
    private final EventRepository eventRepository;
    private final ChurchService churchService;

    // ── Platform Stats ────────────────────
    // GET /api/super-admin/stats
    @GetMapping("/stats")
    public ResponseEntity<?> getPlatformStats() {

        long totalChurches = churchRepository.count();
        long verifiedChurches = churchRepository
                .countByVerifiedTrue();
        long pendingChurches =
                totalChurches - verifiedChurches;
        long totalUsers = userRepository.count();
        long totalSermons = sermonRepository.count();
        long totalEvents = eventRepository.count();

        return ResponseEntity.ok(Map.of(
                "totalChurches", totalChurches,
                "verifiedChurches", verifiedChurches,
                "pendingChurches", pendingChurches,
                "totalUsers", totalUsers,
                "totalSermons", totalSermons,
                "totalEvents", totalEvents
        ));
    }

    // ── Get All Churches ──────────────────
    // GET /api/super-admin/churches
    @GetMapping("/churches")
    public ResponseEntity<?> getAllChurches() {
        List<Church> churches =
                churchRepository.findAll();

        List<Map<String, Object>> response =
                churches.stream().map(church -> {
                    Map<String, Object> data =
                            new java.util.HashMap<>();
                    data.put("id", church.getId());
                    data.put("name", church.getName());
                    data.put("slug", church.getSlug());
                    data.put("city", church.getCity());
                    data.put("state", church.getState());
                    data.put("country",
                            church.getCountry());
                    data.put("logo", church.getLogo());
                    data.put("verified",
                            church.isVerified());
                    data.put("adminName",
                            church.getAdmin().getName());
                    data.put("adminEmail",
                            church.getAdmin().getEmail());
                    data.put("followerCount",
                            followRepository
                                    .countByChurch(church));
                    data.put("sermonCount",
                            sermonRepository
                                    .countByChurch(church));
                    data.put("createdAt",
                            church.getCreatedAt());
                    return data;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ── Verify Church ─────────────────────
    // PUT /api/super-admin/churches/{id}/verify
    @PutMapping("/churches/{id}/verify")
    public ResponseEntity<?> verifyChurch(
            @PathVariable Long id) {

        Church church = churchRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Church not found!"
                        )
                );

        church.setVerified(true);
        churchRepository.save(church);

        return ResponseEntity.ok(Map.of(
                "message",
                church.getName() + " has been verified! ✅"
        ));
    }

    // ── Unverify Church ───────────────────
    // PUT /api/super-admin/churches/{id}/unverify
    @PutMapping("/churches/{id}/unverify")
    public ResponseEntity<?> unverifyChurch(
            @PathVariable Long id) {

        Church church = churchRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Church not found!"
                        )
                );

        church.setVerified(false);
        churchRepository.save(church);

        return ResponseEntity.ok(Map.of(
                "message",
                church.getName() +
                        " verification removed!"
        ));
    }

    // ── Delete Church ─────────────────────
    // DELETE /api/super-admin/churches/{id}
    @DeleteMapping("/churches/{id}")
    public ResponseEntity<?> deleteChurch(
            @PathVariable Long id) {
        try {
            // Use ChurchService which handles
            // all related deletions
            churchService.deleteChurch(id);
            return ResponseEntity.ok(Map.of(
                    "message",
                    "Church deleted successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }
    // ── Get All Users ─────────────────────
    // GET /api/super-admin/users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<Map<String, Object>> response =
                users.stream().map(user -> {
                    Map<String, Object> data =
                            new java.util.HashMap<>();
                    data.put("id", user.getId());
                    data.put("name", user.getName());
                    data.put("email", user.getEmail());
                    data.put("role",
                            user.getRole().name());
                    data.put("profilePic",
                            user.getProfilePic());
                    data.put("enabled",
                            user.isEnabled());
                    data.put("createdAt",
                            user.getCreatedAt());
                    return data;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ── Toggle User Status ────────────────
    // PUT /api/super-admin/users/{id}/toggle
    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleUserStatus(
            @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found!"
                        )
                );

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message",
                user.getName() + " is now " +
                        (user.isEnabled() ?
                                "enabled ✅" : "disabled ❌")
        ));
    }
}