package com.church.church_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SermonRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    // For now we store YouTube/Vimeo URL
    // Later we'll upload to Cloudinary
    private String videoUrl;

    private String audioUrl;

    private String thumbnailUrl;

    @NotBlank(message = "Speaker name is required")
    private String speaker;

    private String series;
}