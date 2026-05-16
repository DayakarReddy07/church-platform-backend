package com.church.church_platform.service;

import com.church.church_platform.dto.request.PostRequest;
import com.church.church_platform.dto.response.PostResponse;
import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;

    // ─── Helper: Get current user ─────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // ─── Helper: Get current user's church ────────────
    private Church getCurrentUserChurch() {
        User user = getCurrentUser();
        return churchRepository.findByAdmin(user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "You don't have a registered church!"
                        )
                );
    }

    // ─── Helper: Map Post → PostResponse ──────────────
    private PostResponse mapToResponse(Post post) {
        Long likeCount = likeRepository.countByPost(post);
        Long commentCount = commentRepository.countByPost(post);

        boolean isLiked = false;
        try {
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            if (email != null && !email.equals("anonymousUser")) {
                User user = userRepository
                        .findByEmail(email).orElse(null);
                if (user != null) {
                    isLiked = likeRepository
                            .existsByUserAndPost(user, post);
                }
            }
        } catch (Exception ignored) {}

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .type(post.getType() != null ?
                        post.getType().name() : "GENERAL")
                .churchId(post.getChurch().getId())
                .churchName(post.getChurch().getName())
                .churchLogo(post.getChurch().getLogo())
                .authorName(post.getAuthor().getName())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 📝 Create post (Church Admin)
    public PostResponse createPost(PostRequest request) {
        User currentUser = getCurrentUser();
        Church church = getCurrentUserChurch();

        Post.PostType type = Post.PostType.GENERAL;
        if (request.getType() != null) {
            try {
                type = Post.PostType.valueOf(
                        request.getType().toUpperCase()
                );
            } catch (Exception ignored) {}
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .type(type)
                .church(church)
                .author(currentUser)
                .build();

        postRepository.save(post);
        return mapToResponse(post);
    }

    // 🌐 Get all posts (Public)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔍 Get single post (Public)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Post not found!")
                );
        return mapToResponse(post);
    }

    // 🏛️ Get posts by church (Public)
    public List<PostResponse> getPostsByChurch(Long churchId) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );
        return postRepository
                .findByChurchOrderByCreatedAtDesc(church)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 👍 Like or Unlike post (Toggle)
    public Map<String, Object> toggleLike(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new RuntimeException("Post not found!")
                );

        if (likeRepository.existsByUserAndPost(currentUser, post)) {
            // Already liked → Unlike
            Like like = likeRepository
                    .findByUserAndPost(currentUser, post).get();
            likeRepository.delete(like);
            return Map.of(
                    "message", "Post unliked",
                    "isLiked", false,
                    "likeCount", likeRepository.countByPost(post)
            );
        } else {
            // Not liked → Like
            Like like = Like.builder()
                    .user(currentUser)
                    .post(post)
                    .build();
            likeRepository.save(like);
            return Map.of(
                    "message", "Post liked!",
                    "isLiked", true,
                    "likeCount", likeRepository.countByPost(post)
            );
        }
    }

    // 🗑️ Delete post (Church Admin)
    public void deletePost(Long id) {
        Church church = getCurrentUserChurch();
        Post post = postRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Post not found!")
                );

        if (!post.getChurch().getId().equals(church.getId())) {
            throw new RuntimeException(
                    "You are not authorized to delete this post!"
            );
        }

        postRepository.delete(post);
    }
}