package com.church.church_platform.service;

import com.church.church_platform.dto.request.*;
import com.church.church_platform.dto.response.ChurchResponse;
import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChurchService {

    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final SermonRepository sermonRepository;
    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final PrayerRequestRepository prayerRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    // ─── Helper: Get logged in user ───────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // ─── Helper: Generate slug from church name ────────────
    // "Grace Fellowship" → "grace-fellowship"
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    // ─── Helper: Map Church to ChurchResponse ─────────────
    private ChurchResponse mapToResponse(Church church) {
        Long followerCount = followRepository
                .countByChurch(church);

        return ChurchResponse.builder()
                .id(church.getId())
                .name(church.getName())
                .slug(church.getSlug())
                .description(church.getDescription())
                .logo(church.getLogo())
                .location(church.getLocation())
                .city(church.getCity())
                .state(church.getState())
                .country(church.getCountry())
                .website(church.getWebsite())
                .phone(church.getPhone())
                .verified(church.isVerified())
                .adminName(church.getAdmin().getName())
                .adminEmail(church.getAdmin().getEmail())
                .followerCount(followerCount)
                .createdAt(church.getCreatedAt())
                .build();
    }

    // Register a church
    public ChurchResponse registerChurch(
            ChurchRegisterRequest request) {

        User currentUser = getCurrentUser();

        // Check 1 — Same admin already has a church
        if (churchRepository
                .findByAdmin(currentUser)
                .isPresent()) {
            throw new RuntimeException(
                    "You already have a registered church!"
            );
        }

        // Check 2 — Church name already exists ← ADD THIS
        if (churchRepository
                .existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException(
                    "A church with this name already exists! " +
                            "Please use a different name."
            );
        }

        // Generate unique slug
        String slug = generateSlug(request.getName());

        // If slug exists add number to end
        if (churchRepository.existsBySlug(slug)) {
            slug = slug + "-" +
                    System.currentTimeMillis();
        }

        // Create church
        Church church = Church.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .location(request.getLocation())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .website(request.getWebsite())
                .phone(request.getPhone())
                .admin(currentUser)
                .verified(false)
                .build();

        churchRepository.save(church);
        return mapToResponse(church);
    }

    // 🏛️ Get all public churches
    public List<ChurchResponse> getAllPublicChurches() {
        return churchRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔍 Get church by slug (public profile)
    public ChurchResponse getChurchBySlug(String slug) {
        Church church = churchRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );
        return mapToResponse(church);
    }

    // 👤 Get my church (church admin)
    public ChurchResponse getMyChurch() {
        User currentUser = getCurrentUser();
        Church church = churchRepository.findByAdmin(currentUser)
                .orElseThrow(() ->
                        new RuntimeException(
                                "You don't have a registered church!"
                        )
                );
        return mapToResponse(church);
    }

    // ✏️ Update church profile
    public ChurchResponse updateChurch(
            Long id, ChurchUpdateRequest request) {

        User currentUser = getCurrentUser();
        Church church = churchRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );

        // Make sure only the church admin can update
        if (!church.getAdmin().getId()
                .equals(currentUser.getId())) {
            throw new RuntimeException(
                    "You are not authorized to update this church!"
            );
        }

        // Update only fields that are provided
        if (request.getName() != null)
            church.setName(request.getName());
        if (request.getDescription() != null)
            church.setDescription(request.getDescription());
        if (request.getLocation() != null)
            church.setLocation(request.getLocation());
        if (request.getCity() != null)
            church.setCity(request.getCity());
        if (request.getState() != null)
            church.setState(request.getState());
        if (request.getCountry() != null)
            church.setCountry(request.getCountry());
        if (request.getWebsite() != null)
            church.setWebsite(request.getWebsite());
        if (request.getPhone() != null)
            church.setPhone(request.getPhone());
        if (request.getLogo() != null)
            church.setLogo(request.getLogo());

        churchRepository.save(church);
        return mapToResponse(church);
    }

    // 🔍 Search churches by name or city
    public List<ChurchResponse> searchChurches(String keyword) {
        return churchRepository
                .findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(
                        keyword, keyword
                )
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🗑️ Delete church and all related data
    public void deleteChurch(Long churchId) {
        Church church = churchRepository
                .findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );

        // Step 1 → Delete follows first
        List<Follow> follows = followRepository
                .findByChurch(church);
        followRepository.deleteAll(follows);

        // Step 2 → Delete sermons
        List<Sermon> sermons = sermonRepository
                .findByChurchOrderByCreatedAtDesc(church);
        sermonRepository.deleteAll(sermons);

        // Step 3 → Delete events
        List<Event> events = eventRepository
                .findByChurchOrderByEventDateAsc(church);
        eventRepository.deleteAll(events);

        // Step 4 → Delete prayer requests
        List<PrayerRequest> prayers = prayerRepository
                .findByChurch(church);
        prayerRepository.deleteAll(prayers);

        // Step 5 → Delete posts (with likes & comments)
        List<Post> posts = postRepository
                .findByChurchOrderByCreatedAtDesc(church);
        posts.forEach(post -> deletePostWithRelations(post));

        // Step 6 → Finally delete church
        churchRepository.delete(church);
    }
    // Helper — delete post with its likes & comments
    private void deletePostWithRelations(Post post) {

        // Delete likes first
        List<Like> likes = likeRepository
                .findByPost(post);
        likeRepository.deleteAll(likes);

        // Delete comments
        List<Comment> comments = commentRepository
                .findByPostOrderByCreatedAtDesc(post);
        commentRepository.deleteAll(comments);

        // Delete post
        postRepository.delete(post);
    }
}