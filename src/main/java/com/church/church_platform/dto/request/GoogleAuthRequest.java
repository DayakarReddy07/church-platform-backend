package com.church.church_platform.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {
    // Token from Google sent by frontend
    private String token;
    // Role user selected (MEMBER or CHURCH_ADMIN)
    private String role;
}