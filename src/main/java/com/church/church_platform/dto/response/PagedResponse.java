package com.church.church_platform.dto.response;

import lombok.*;
import java.util.List;

// Generic paged response for any type
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponse<T> {

    private List<T> content;      // actual data
    private int currentPage;      // current page number
    private int totalPages;       // total number of pages
    private long totalElements;   // total records
    private boolean isLast;       // is this last page?
}