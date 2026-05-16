package com.church.church_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SubmitPrayerRequest {

    @NotBlank(message = "Prayer content is required")
    private String content;

    private boolean isPublic = true;

    // Optional — can submit to specific church
    private Long churchId;
}