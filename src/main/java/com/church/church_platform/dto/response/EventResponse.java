package com.church.church_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String imageUrl;
    private LocalDateTime eventDate;
    private boolean isOnline;
    private String meetingLink;

    // Church info
    private Long churchId;
    private String churchName;
    private String churchLogo;

    // Registration info
    private Long registrationCount;
    private boolean isRegistered;

    private LocalDateTime createdAt;
}