package com.church.church_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SermonResponse {

    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private String audioUrl;
    private String thumbnailUrl;
    private String speaker;
    private String series;

    // Church info
    private Long churchId;
    private String churchName;
    private String churchSlug;
    private String churchLogo;

    private LocalDateTime createdAt;
}