package com.church.church_platform.service;

import com.church.church_platform.dto.response.*;
import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserRepository userRepository;
    private final ChurchRepository churchRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final SermonRepository sermonRepository;
    private final EventRepository eventRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final EventRegistrationRepository registrationRepository;

    // ─── Helper: Get current user ─────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // ─── Helper: Map Post → FeedItem ──────────────────
    private FeedItemResponse mapPostToFeedItem(
            Post post, User currentUser) {

        Long likeCount = likeRepository.countByPost(post);
        Long commentCount = commentRepository.countByPost(post);
        boolean isLiked = likeRepository
                .existsByUserAndPost(currentUser, post);

        return FeedItemResponse.builder()
                .itemType("POST")
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .postType(post.getType() != null ?
                        post.getType().name() : "GENERAL")
                .churchId(post.getChurch().getId())
                .churchName(post.getChurch().getName())
                .churchSlug(post.getChurch().getSlug())
                .churchLogo(post.getChurch().getLogo())
                .authorName(post.getAuthor().getName())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .build();
    }

    // ─── Helper: Map Sermon → FeedItem ────────────────
    private FeedItemResponse mapSermonToFeedItem(Sermon sermon) {
        return FeedItemResponse.builder()
                .itemType("SERMON")
                .id(sermon.getId())
                .title(sermon.getTitle())
                .content(sermon.getDescription())
                .imageUrl(sermon.getThumbnailUrl())
                .videoUrl(sermon.getVideoUrl())
                .speaker(sermon.getSpeaker())
                .series(sermon.getSeries())
                .churchId(sermon.getChurch().getId())
                .churchName(sermon.getChurch().getName())
                .churchSlug(sermon.getChurch().getSlug())
                .churchLogo(sermon.getChurch().getLogo())
                .createdAt(sermon.getCreatedAt())
                .build();
    }

    // ─── Helper: Map Event → FeedItem ─────────────────
    private FeedItemResponse mapEventToFeedItem(
            Event event, User currentUser) {

        Long registrationCount = registrationRepository
                .countByEvent(event);
        boolean isRegistered = registrationRepository
                .existsByUserAndEvent(currentUser, event);

        return FeedItemResponse.builder()
                .itemType("EVENT")
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getDescription())
                .imageUrl(event.getImageUrl())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .isOnline(event.isOnline())
                .registrationCount(registrationCount)
                .churchId(event.getChurch().getId())
                .churchName(event.getChurch().getName())
                .churchSlug(event.getChurch().getSlug())
                .churchLogo(event.getChurch().getLogo())
                .createdAt(event.getCreatedAt())
                .build();
    }

    // 🏠 Get personalized feed
    public List<FeedItemResponse> getPersonalizedFeed() {
        User currentUser = getCurrentUser();

        // Get all churches user follows
        List<Church> followedChurches = followRepository
                .findByUser(currentUser)
                .stream()
                .map(Follow::getChurch)
                .collect(Collectors.toList());

        // If not following any church →
        // return discover feed instead
        if (followedChurches.isEmpty()) {
            return getDiscoverFeed();
        }

        List<FeedItemResponse> feedItems = new ArrayList<>();

        // Add posts from followed churches
        for (Church church : followedChurches) {
            postRepository
                    .findByChurchOrderByCreatedAtDesc(church)
                    .forEach(post ->
                            feedItems.add(
                                    mapPostToFeedItem(post, currentUser)
                            )
                    );

            // Add sermons from followed churches
            sermonRepository
                    .findByChurchOrderByCreatedAtDesc(church)
                    .forEach(sermon ->
                            feedItems.add(mapSermonToFeedItem(sermon))
                    );

            // Add events from followed churches
            eventRepository
                    .findByChurchOrderByEventDateAsc(church)
                    .forEach(event ->
                            feedItems.add(
                                    mapEventToFeedItem(event, currentUser)
                            )
                    );
        }

        // Sort everything by createdAt (newest first)
        feedItems.sort((a, b) ->
                b.getCreatedAt().compareTo(a.getCreatedAt())
        );

        return feedItems;
    }

    // 🌐 Get discover feed (all churches content)
    public List<FeedItemResponse> getDiscoverFeed() {
        User currentUser = getCurrentUser();
        List<FeedItemResponse> feedItems = new ArrayList<>();

        // Add all posts
        postRepository.findAllByOrderByCreatedAtDesc()
                .forEach(post ->
                        feedItems.add(
                                mapPostToFeedItem(post, currentUser)
                        )
                );

        // Add all sermons
        sermonRepository
                .findAllByOrderByCreatedAtDesc(
                        org.springframework.data.domain.Pageable.unpaged()
                )
                .forEach(sermon ->
                        feedItems.add(mapSermonToFeedItem(sermon))
                );

        // Add upcoming events only
        eventRepository
                .findUpcomingEvents(LocalDateTime.now())
                .forEach(event ->
                        feedItems.add(
                                mapEventToFeedItem(event, currentUser)
                        )
                );

        // Sort by newest first
        feedItems.sort((a, b) ->
                b.getCreatedAt().compareTo(a.getCreatedAt())
        );

        return feedItems;
    }

    // 📊 Get member dashboard stats
    public FeedStatsResponse getFeedStats() {
        User currentUser = getCurrentUser();

        // Get followed churches
        List<Follow> follows = followRepository
                .findByUser(currentUser);
        List<Church> followedChurches = follows.stream()
                .map(Follow::getChurch)
                .collect(Collectors.toList());

        Long followingCount = (long) followedChurches.size();

        // Count content from followed churches
        long totalSermons = 0;
        long totalEvents = 0;
        long totalPosts = 0;

        for (Church church : followedChurches) {
            totalSermons += sermonRepository
                    .countByChurch(church);
            totalEvents += eventRepository
                    .countByChurch(church);
            totalPosts += postRepository
                    .findByChurchOrderByCreatedAtDesc(church)
                    .size();
        }

        // Get upcoming events from followed churches
        List<EventResponse> upcomingEvents = new ArrayList<>();
        for (Church church : followedChurches) {
            eventRepository
                    .findUpcomingEventsByChurch(
                            church, LocalDateTime.now()
                    )
                    .stream()
                    .limit(3) // max 3 per church
                    .forEach(event -> {
                        upcomingEvents.add(EventResponse.builder()
                                .id(event.getId())
                                .title(event.getTitle())
                                .eventDate(event.getEventDate())
                                .location(event.getLocation())
                                .churchName(event.getChurch().getName())
                                .build());
                    });
        }

        // Suggest churches user is NOT following
        List<Church> allChurches = churchRepository.findAll();
        List<ChurchResponse> suggestedChurches = allChurches
                .stream()
                .filter(church -> !followedChurches.contains(church))
                .limit(5)
                .map(church -> ChurchResponse.builder()
                        .id(church.getId())
                        .name(church.getName())
                        .slug(church.getSlug())
                        .city(church.getCity())
                        .logo(church.getLogo())
                        .followerCount(
                                followRepository.countByChurch(church)
                        )
                        .build())
                .collect(Collectors.toList());

        return FeedStatsResponse.builder()
                .followingCount(followingCount)
                .totalSermons(totalSermons)
                .totalEvents(totalEvents)
                .totalPosts(totalPosts)
                .upcomingEvents(upcomingEvents)
                .suggestedChurches(suggestedChurches)
                .build();
    }
}