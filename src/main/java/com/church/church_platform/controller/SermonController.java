package com.church.church_platform.controller;

import com.church.church_platform.dto.request.SermonRequest;
import com.church.church_platform.dto.response.*;
import com.church.church_platform.service.SermonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SermonController {

    private final SermonService sermonService;

    // 📤 Upload sermon
    // POST http://localhost:8080/api/sermons/upload
    @PostMapping("/api/sermons/upload")
    public ResponseEntity<SermonResponse> uploadSermon(
            @Valid @RequestBody SermonRequest request) {
        return ResponseEntity.ok(
                sermonService.uploadSermon(request)
        );
    }

    // 🌐 Get all sermons with pagination
    // GET http://localhost:8080/api/sermons/public?page=0&size=10
    @GetMapping("/api/sermons/public")
    public ResponseEntity<PagedResponse<SermonResponse>> getAllSermons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                sermonService.getAllSermons(page, size)
        );
    }

    // 🔍 Get single sermon
    // GET http://localhost:8080/api/sermons/public/{id}
    @GetMapping("/api/sermons/public/{id}")
    public ResponseEntity<SermonResponse> getSermonById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                sermonService.getSermonById(id)
        );
    }

    // 🏛️ Get sermons by church
    // GET http://localhost:8080/api/sermons/public/church/{churchId}
    @GetMapping("/api/sermons/public/church/{churchId}")
    public ResponseEntity<List<SermonResponse>> getSermonsByChurch(
            @PathVariable Long churchId) {
        return ResponseEntity.ok(
                sermonService.getSermonsByChurch(churchId)
        );
    }

    // 👤 Get my sermons (Church Admin)
    // GET http://localhost:8080/api/sermons/my-sermons
    @GetMapping("/api/sermons/my-sermons")
    public ResponseEntity<List<SermonResponse>> getMySermons() {
        return ResponseEntity.ok(sermonService.getMySermons());
    }

    // ✏️ Update sermon
    // PUT http://localhost:8080/api/sermons/{id}
    @PutMapping("/api/sermons/{id}")
    public ResponseEntity<SermonResponse> updateSermon(
            @PathVariable Long id,
            @RequestBody SermonRequest request) {
        return ResponseEntity.ok(
                sermonService.updateSermon(id, request)
        );
    }

    // 🗑️ Delete sermon
    // DELETE http://localhost:8080/api/sermons/{id}
    @DeleteMapping("/api/sermons/{id}")
    public ResponseEntity<Void> deleteSermon(
            @PathVariable Long id) {
        sermonService.deleteSermon(id);
        return ResponseEntity.noContent().build();
    }

    // 🔍 Search sermons
    // GET http://localhost:8080/api/sermons/search?keyword=grace
    @GetMapping("/api/sermons/search")
    public ResponseEntity<List<SermonResponse>> searchSermons(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                sermonService.searchSermons(keyword)
        );
    }
}