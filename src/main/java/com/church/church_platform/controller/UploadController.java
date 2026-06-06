package com.church.church_platform.controller;

import com.church.church_platform.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UploadController {
    private final CloudinaryService cloudinaryService;
    // Upload profile picture
    // POST /api/upload/profile
    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfile(
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            validateImage(file);

            String url = cloudinaryService
                    .uploadImage(file, "profiles");

            return ResponseEntity.ok(
                    Map.of(
                            "url", url,
                            "message", "Profile picture uploaded!"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Upload church logo
    // POST /api/upload/church-logo
    @PostMapping("/church-logo")
    public ResponseEntity<?> uploadChurchLogo(
            @RequestParam("file") MultipartFile file) {
        try {
            validateImage(file);

            String url = cloudinaryService
                    .uploadImage(file, "churches");

            return ResponseEntity.ok(
                    Map.of(
                            "url", url,
                            "message", "Church logo uploaded!"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Upload post image
    // POST /api/upload/post-image
    @PostMapping("/post-image")
    public ResponseEntity<?> uploadPostImage(
            @RequestParam("file") MultipartFile file) {
        try {
            validateImage(file);

            String url = cloudinaryService
                    .uploadImage(file, "posts");

            return ResponseEntity.ok(
                    Map.of(
                            "url", url,
                            "message", "Image uploaded!"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Upload sermon thumbnail
    // POST /api/upload/sermon-thumbnail
    @PostMapping("/sermon-thumbnail")
    public ResponseEntity<?> uploadSermonThumbnail(
            @RequestParam("file") MultipartFile file) {
        try {
            validateImage(file);

            String url = cloudinaryService
                    .uploadImage(file, "sermons");

            return ResponseEntity.ok(
                    Map.of(
                            "url", url,
                            "message", "Thumbnail uploaded!"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Validate image file
    private void validateImage(MultipartFile file) {
        // Check file not empty
        if (file.isEmpty()) {
            throw new RuntimeException(
                    "Please select a file!"
            );
        }

        // Check file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException(
                    "File size must be less than 5MB!"
            );
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null ||
                !contentType.startsWith("image/")) {
            throw new RuntimeException(
                    "Only image files are allowed!"
            );
        }
    }
}