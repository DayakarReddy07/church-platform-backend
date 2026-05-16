package com.church.church_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String location;

    private String imageUrl;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    private boolean isOnline = false;

    private String meetingLink;
}