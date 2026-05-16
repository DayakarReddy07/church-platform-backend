package com.church.church_platform.repository;

import com.church.church_platform.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, Long> {

    // Check if user already registered
    boolean existsByUserAndEvent(User user, Event event);

    // Find registration record
    Optional<EventRegistration> findByUserAndEvent(
            User user, Event event
    );

    // Get all registrations for an event
    List<EventRegistration> findByEvent(Event event);

    // Get all events a user registered for
    List<EventRegistration> findByUser(User user);

    // Count registrations for an event
    Long countByEvent(Event event);
}