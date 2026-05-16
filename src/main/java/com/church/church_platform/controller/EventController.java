package com.church.church_platform.controller;

import com.church.church_platform.dto.request.EventRequest;
import com.church.church_platform.dto.response.EventResponse;
import com.church.church_platform.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    private final EventService eventService;

    // 📅 Create event
    @PostMapping("/api/events/create")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(
                eventService.createEvent(request)
        );
    }

    // 🌐 Get all events (Public)
    @GetMapping("/api/events/public")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // 🔍 Get single event (Public)
    @GetMapping("/api/events/public/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // 🏛️ Get events by church (Public)
    @GetMapping("/api/events/public/church/{churchId}")
    public ResponseEntity<List<EventResponse>> getEventsByChurch(
            @PathVariable Long churchId) {
        return ResponseEntity.ok(
                eventService.getEventsByChurch(churchId)
        );
    }

    // 📅 Get upcoming events (Public)
    @GetMapping("/api/events/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(
                eventService.getUpcomingEvents()
        );
    }

    // ✅ Register for event
    @PostMapping("/api/events/{id}/register")
    public ResponseEntity<Map<String, String>> registerForEvent(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                eventService.registerForEvent(id)
        );
    }

    // ❌ Cancel registration
    @DeleteMapping("/api/events/{id}/register")
    public ResponseEntity<Map<String, String>> cancelRegistration(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                eventService.cancelRegistration(id)
        );
    }

    // 📋 Get my registrations
    @GetMapping("/api/events/my-registrations")
    public ResponseEntity<List<EventResponse>> getMyRegistrations() {
        return ResponseEntity.ok(
                eventService.getMyRegistrations()
        );
    }

    // ✏️ Update event
    @PutMapping("/api/events/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request) {
        return ResponseEntity.ok(
                eventService.updateEvent(id, request)
        );
    }

    // 🗑️ Delete event
    @DeleteMapping("/api/events/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}