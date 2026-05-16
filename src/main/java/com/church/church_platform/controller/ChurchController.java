package com.church.church_platform.controller;

import com.church.church_platform.dto.request.*;
import com.church.church_platform.dto.response.ChurchResponse;
import com.church.church_platform.service.ChurchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ChurchController {

    private final ChurchService churchService;

    // 📝 Register new church (Church Admin only)
    // POST http://localhost:8080/api/churches/register
    @PostMapping("/api/churches/register")
    public ResponseEntity<ChurchResponse> registerChurch(
            @Valid @RequestBody ChurchRegisterRequest request) {
        return ResponseEntity.ok(
                churchService.registerChurch(request)
        );
    }

    // 🌐 Get all churches (Public)
    // GET http://localhost:8080/api/churches/public
    @GetMapping("/api/churches/public")
    public ResponseEntity<List<ChurchResponse>> getAllChurches() {
        return ResponseEntity.ok(
                churchService.getAllPublicChurches()
        );
    }

    // 🔍 Get church by slug (Public)
    // GET http://localhost:8080/api/churches/public/grace-fellowship
    @GetMapping("/api/churches/public/{slug}")
    public ResponseEntity<ChurchResponse> getChurchBySlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(
                churchService.getChurchBySlug(slug)
        );
    }

    // 👤 Get my church (Church Admin)
    // GET http://localhost:8080/api/churches/my-church
    @GetMapping("/api/churches/my-church")
    public ResponseEntity<ChurchResponse> getMyChurch() {
        return ResponseEntity.ok(churchService.getMyChurch());
    }

    // ✏️ Update church (Church Admin)
    // PUT http://localhost:8080/api/churches/{id}
    @PutMapping("/api/churches/{id}")
    public ResponseEntity<ChurchResponse> updateChurch(
            @PathVariable Long id,
            @RequestBody ChurchUpdateRequest request) {
        return ResponseEntity.ok(
                churchService.updateChurch(id, request)
        );
    }

    // 🔍 Search churches
    // GET http://localhost:8080/api/churches/search?keyword=grace
    @GetMapping("/api/churches/search")
    public ResponseEntity<List<ChurchResponse>> searchChurches(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                churchService.searchChurches(keyword)
        );
    }
}