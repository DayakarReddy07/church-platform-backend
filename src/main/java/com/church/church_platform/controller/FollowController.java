package com.church.church_platform.controller;

import com.church.church_platform.dto.response.ChurchResponse;
import com.church.church_platform.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FollowController {

    private final FollowService followService;

    // 🤝 Follow a church
    // POST http://localhost:8080/api/follows/{churchId}
    @PostMapping("/{churchId}")
    public ResponseEntity<Map<String, String>> followChurch(
            @PathVariable Long churchId) {
        return ResponseEntity.ok(
                followService.followChurch(churchId)
        );
    }

    // 💔 Unfollow a church
    // DELETE http://localhost:8080/api/follows/{churchId}
    @DeleteMapping("/{churchId}")
    public ResponseEntity<Map<String, String>> unfollowChurch(
            @PathVariable Long churchId) {
        return ResponseEntity.ok(
                followService.unfollowChurch(churchId)
        );
    }

    // 📋 Get all my followed churches
    // GET http://localhost:8080/api/follows/my-churches
    @GetMapping("/my-churches")
    public ResponseEntity<List<ChurchResponse>> getMyChurches() {
        return ResponseEntity.ok(
                followService.getMyFollowedChurches()
        );
    }

    // ✅ Check follow status
    // GET http://localhost:8080/api/follows/{churchId}/status
    @GetMapping("/{churchId}/status")
    public ResponseEntity<Map<String, Object>> checkStatus(
            @PathVariable Long churchId) {
        return ResponseEntity.ok(
                followService.checkFollowStatus(churchId)
        );
    }
}