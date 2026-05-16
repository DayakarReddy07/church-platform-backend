package com.church.church_platform.service;

import com.church.church_platform.dto.request.EventRequest;
import com.church.church_platform.dto.response.EventResponse;
import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;

    // ─── Helper: Get logged in user ───────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // ─── Helper: Get church of logged in admin ─────────
    private Church getCurrentUserChurch() {
        User currentUser = getCurrentUser();
        return churchRepository.findByAdmin(currentUser)
                .orElseThrow(() ->
                        new RuntimeException(
                                "You don't have a registered church!"
                        )
                );
    }

    // ─── Helper: Map Event to EventResponse ───────────
    private EventResponse mapToResponse(Event event) {
        Long registrationCount = registrationRepository
                .countByEvent(event);

        // Check if current user registered
        // (safe check — works even if not logged in)
        boolean isRegistered = false;
        try {
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            if (email != null && !email.equals("anonymousUser")) {
                User user = userRepository
                        .findByEmail(email).orElse(null);
                if (user != null) {
                    isRegistered = registrationRepository
                            .existsByUserAndEvent(user, event);
                }
            }
        } catch (Exception ignored) {}

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageUrl(event.getImageUrl())
                .eventDate(event.getEventDate())
                .isOnline(event.isOnline())
                .meetingLink(event.getMeetingLink())
                .churchId(event.getChurch().getId())
                .churchName(event.getChurch().getName())
                .churchLogo(event.getChurch().getLogo())
                .registrationCount(registrationCount)
                .isRegistered(isRegistered)
                .createdAt(event.getCreatedAt())
                .build();
    }

    // 📅 Create event (Church Admin)
    public EventResponse createEvent(EventRequest request) {
        Church church = getCurrentUserChurch();

        // Event date must be in future
        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Event date must be in the future!"
            );
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .imageUrl(request.getImageUrl())
                .eventDate(request.getEventDate())
                .isOnline(request.isOnline())
                .meetingLink(request.getMeetingLink())
                .church(church)
                .build();

        eventRepository.save(event);
        return mapToResponse(event);
    }

    // 🌐 Get all events (Public)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔍 Get single event (Public)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found!")
                );
        return mapToResponse(event);
    }

    // 🏛️ Get events by church (Public)
    public List<EventResponse> getEventsByChurch(Long churchId) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );
        return eventRepository
                .findByChurchOrderByEventDateAsc(church)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 📅 Get upcoming events (Public)
    public List<EventResponse> getUpcomingEvents() {
        return eventRepository
                .findUpcomingEvents(LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Register for event (Member)
    public Map<String, String> registerForEvent(Long eventId) {
        User currentUser = getCurrentUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found!")
                );

        // Check if event is in future
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Cannot register for past events!"
            );
        }

        // Check if already registered
        if (registrationRepository.existsByUserAndEvent(
                currentUser, event)) {
            throw new RuntimeException(
                    "You are already registered for this event!"
            );
        }

        EventRegistration registration = EventRegistration.builder()
                .user(currentUser)
                .event(event)
                .build();

        registrationRepository.save(registration);

        return Map.of(
                "message", "Successfully registered for "
                        + event.getTitle()
        );
    }

    // ❌ Cancel registration (Member)
    public Map<String, String> cancelRegistration(Long eventId) {
        User currentUser = getCurrentUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found!")
                );

        EventRegistration registration = registrationRepository
                .findByUserAndEvent(currentUser, event)
                .orElseThrow(() ->
                        new RuntimeException(
                                "You are not registered for this event!"
                        )
                );

        registrationRepository.delete(registration);

        return Map.of(
                "message", "Registration cancelled for "
                        + event.getTitle()
        );
    }

    // 📋 Get my registered events (Member)
    public List<EventResponse> getMyRegistrations() {
        User currentUser = getCurrentUser();
        return registrationRepository
                .findByUser(currentUser)
                .stream()
                .map(reg -> mapToResponse(reg.getEvent()))
                .collect(Collectors.toList());
    }

    // ✏️ Update event (Church Admin)
    public EventResponse updateEvent(
            Long id, EventRequest request) {

        Church church = getCurrentUserChurch();
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found!")
                );

        // Make sure event belongs to this church
        if (!event.getChurch().getId().equals(church.getId())) {
            throw new RuntimeException(
                    "You are not authorized to update this event!"
            );
        }

        if (request.getTitle() != null)
            event.setTitle(request.getTitle());
        if (request.getDescription() != null)
            event.setDescription(request.getDescription());
        if (request.getLocation() != null)
            event.setLocation(request.getLocation());
        if (request.getImageUrl() != null)
            event.setImageUrl(request.getImageUrl());
        if (request.getEventDate() != null)
            event.setEventDate(request.getEventDate());
        if (request.getMeetingLink() != null)
            event.setMeetingLink(request.getMeetingLink());

        eventRepository.save(event);
        return mapToResponse(event);
    }

    // 🗑️ Delete event (Church Admin)
    public void deleteEvent(Long id) {
        Church church = getCurrentUserChurch();
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found!")
                );

        if (!event.getChurch().getId().equals(church.getId())) {
            throw new RuntimeException(
                    "You are not authorized to delete this event!"
            );
        }

        eventRepository.delete(event);
    }
}