package com.church.church_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedItemResponse {

    // What type of item is this?
    // POST, SERMON, EVENT
    private String itemType;

    private Long id;
    private String title;
    private String content;
    private String imageUrl;

    // For sermons
    private String videoUrl;
    private String speaker;
    private String series;

    // For events
    private LocalDateTime eventDate;
    private String location;
    private boolean isOnline;
    private Long registrationCount;

    // For posts
    private Long likeCount;
    private Long commentCount;
    private boolean isLiked;
    private String postType;

    // Church info (same for all types)
    private Long churchId;
    private String churchName;
    private String churchSlug;
    private String churchLogo;

    // Author info
    private String authorName;

    // When was it created (used for sorting)
    private LocalDateTime createdAt;
}