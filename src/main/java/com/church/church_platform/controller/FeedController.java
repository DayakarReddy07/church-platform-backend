package com.church.church_platform.controller;

import com.church.church_platform.dto.response.*;
import com.church.church_platform.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FeedController {

    private final FeedService feedService;

    // 🏠 Personalized feed
    // GET http://localhost:8080/api/feed
    @GetMapping
    public ResponseEntity<List<FeedItemResponse>>
    getPersonalizedFeed() {
        return ResponseEntity.ok(
                feedService.getPersonalizedFeed()
        );
    }

    // 🌐 Discover feed
    // GET http://localhost:8080/api/feed/discover
    @GetMapping("/discover")
    public ResponseEntity<List<FeedItemResponse>>
    getDiscoverFeed() {
        return ResponseEntity.ok(
                feedService.getDiscoverFeed()
        );
    }

    // 📊 Dashboard stats
    // GET http://localhost:8080/api/feed/stats
    @GetMapping("/stats")
    public ResponseEntity<FeedStatsResponse> getFeedStats() {
        return ResponseEntity.ok(feedService.getFeedStats());
    }
}