package com.church.church_platform.controller;

import com.church.church_platform.dto.request.PostRequest;
import com.church.church_platform.dto.response.PostResponse;
import com.church.church_platform.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;

    @PostMapping("/api/posts/create")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @GetMapping("/api/posts/public")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/api/posts/public/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/api/posts/public/church/{churchId}")
    public ResponseEntity<List<PostResponse>> getPostsByChurch(
            @PathVariable Long churchId) {
        return ResponseEntity.ok(
                postService.getPostsByChurch(churchId)
        );
    }

    @PostMapping("/api/posts/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id) {
        return ResponseEntity.ok(postService.toggleLike(id));
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}