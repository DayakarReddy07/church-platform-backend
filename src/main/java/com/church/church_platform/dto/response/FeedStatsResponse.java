package com.church.church_platform.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedStatsResponse {

    // Member stats
    private Long followingCount;      // churches I follow
    private Long savedSermonsCount;   // sermons I saved

    // Content counts from followed churches
    private Long totalSermons;
    private Long totalEvents;
    private Long totalPosts;
    private Long totalPrayers;

    // Upcoming events from followed churches
    private List<EventResponse> upcomingEvents;

    // Suggested churches to follow
    private List<ChurchResponse> suggestedChurches;
}