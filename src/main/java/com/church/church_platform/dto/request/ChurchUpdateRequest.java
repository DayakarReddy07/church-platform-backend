package com.church.church_platform.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChurchUpdateRequest {

    private String name;
    private String description;
    private String location;
    private String city;
    private String state;
    private String country;
    private String website;
    private String phone;
    private String logo;
}