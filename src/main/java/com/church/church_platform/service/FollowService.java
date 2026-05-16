package com.church.church_platform.service;

import com.church.church_platform.dto.response.ChurchResponse;
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
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ChurchRepository churchRepository;

    // ─── Helper: Get logged in user ───────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // 🤝 Follow a church
    public Map<String, String> followChurch(Long churchId) {
        User currentUser = getCurrentUser();

        Church church = churchRepository.findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );

        // Check if already following
        if (followRepository.existsByUserAndChurch(
                currentUser, church)) {
            throw new RuntimeException(
                    "You are already following this church!"
            );
        }

        // Create follow record
        Follow follow = Follow.builder()
                .user(currentUser)
                .church(church)
                .build();

        followRepository.save(follow);

        return Map.of(
                "message", "Successfully followed " + church.getName()
        );
    }

    // 💔 Unfollow a church
    public Map<String, String> unfollowChurch(Long churchId) {
        User currentUser = getCurrentUser();

        Church church = churchRepository.findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );

        // Find follow record
        Follow follow = followRepository
                .findByUserAndChurch(currentUser, church)
                .orElseThrow(() ->
                        new RuntimeException(
                                "You are not following this church!"
                        )
                );

        followRepository.delete(follow);

        return Map.of(
                "message", "Successfully unfollowed " + church.getName()
        );
    }

    // 📋 Get all churches I follow
    public List<ChurchResponse> getMyFollowedChurches() {
        User currentUser = getCurrentUser();

        return followRepository.findByUser(currentUser)
                .stream()
                .map(follow -> {
                    Church church = follow.getChurch();
                    Long followerCount = followRepository
                            .countByChurch(church);
                    return ChurchResponse.builder()
                            .id(church.getId())
                            .name(church.getName())
                            .slug(church.getSlug())
                            .description(church.getDescription())
                            .logo(church.getLogo())
                            .city(church.getCity())
                            .state(church.getState())
                            .country(church.getCountry())
                            .verified(church.isVerified())
                            .adminName(church.getAdmin().getName())
                            .followerCount(followerCount)
                            .createdAt(church.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ✅ Check if I follow a specific church
    public Map<String, Object> checkFollowStatus(Long churchId) {
        User currentUser = getCurrentUser();

        Church church = churchRepository.findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );

        boolean isFollowing = followRepository
                .existsByUserAndChurch(currentUser, church);

        Long followerCount = followRepository.countByChurch(church);

        return Map.of(
                "isFollowing", isFollowing,
                "followerCount", followerCount,
                "churchName", church.getName()
        );
    }
}