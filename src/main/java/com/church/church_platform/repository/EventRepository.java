package com.church.church_platform.repository;

import com.church.church_platform.entity.Church;
import com.church.church_platform.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository
        extends JpaRepository<Event, Long> {

    // Get all events by church
    List<Event> findByChurchOrderByEventDateAsc(Church church);

    // Get upcoming events (future events only)
    @Query("SELECT e FROM Event e WHERE " +
            "e.eventDate > :now " +
            "ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(LocalDateTime now);

    // Get upcoming events by church
    @Query("SELECT e FROM Event e WHERE " +
            "e.church = :church AND " +
            "e.eventDate > :now " +
            "ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEventsByChurch(
            Church church,
            LocalDateTime now
    );

    // Count events per church
    Long countByChurch(Church church);
}