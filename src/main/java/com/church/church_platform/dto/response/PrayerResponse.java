package com.church.church_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrayerResponse {

    private Long id;
    private String content;
    private boolean isPublic;

    // Who submitted
    private String userName;

    // Church (optional)
    private String churchName;

    // Prayer count
    private Long prayerCount;
    private boolean isPraying;

    private LocalDateTime createdAt;
}