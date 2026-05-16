package com.church.church_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private String title;
    private String imageUrl;
    private String type; // ANNOUNCEMENT, DEVOTIONAL, TESTIMONY, GENERAL
}