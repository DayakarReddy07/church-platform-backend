package com.church.church_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String type;

    // Church info
    private Long churchId;
    private String churchName;
    private String churchLogo;

    // Author info
    private String authorName;

    // Engagement
    private Long likeCount;
    private Long commentCount;
    private boolean isLiked;

    private LocalDateTime createdAt;
}