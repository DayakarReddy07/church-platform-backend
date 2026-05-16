package com.church.church_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChurchResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String logo;
    private String location;
    private String city;
    private String state;
    private String country;
    private String website;
    private String phone;
    private boolean verified;
    private String adminName;
    private String adminEmail;
    private Long followerCount;
    private LocalDateTime createdAt;
}