package com.church.church_platform.controller;

import com.church.church_platform.dto.request.SubmitPrayerRequest;
import com.church.church_platform.dto.response.PrayerResponse;
import com.church.church_platform.service.PrayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PrayerController {

    private final PrayerService prayerService;

    @PostMapping("/api/prayers/submit")
    public ResponseEntity<PrayerResponse> submitPrayer(
            @Valid @RequestBody SubmitPrayerRequest request) {
        return ResponseEntity.ok(
                prayerService.submitPrayer(request)
        );
    }

    @GetMapping("/api/prayers/public")
    public ResponseEntity<List<PrayerResponse>> getPublicPrayers() {
        return ResponseEntity.ok(prayerService.getPublicPrayers());
    }

    @PostMapping("/api/prayers/{id}/pray")
    public ResponseEntity<Map<String, Object>> togglePraying(
            @PathVariable Long id) {
        return ResponseEntity.ok(prayerService.togglePraying(id));
    }

    @GetMapping("/api/prayers/my-prayers")
    public ResponseEntity<List<PrayerResponse>> getMyPrayers() {
        return ResponseEntity.ok(prayerService.getMyPrayers());
    }
}