package com.church.church_platform.service;

import com.church.church_platform.dto.request.SubmitPrayerRequest;
import com.church.church_platform.dto.response.PrayerResponse;
import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrayerService {

    private final PrayerRequestRepository prayerRepository;
    private final PrayerCountRepository prayerCountRepository;
    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;

    // ─── Helper: Get current user ─────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found!")
                );
    }

    // ─── Helper: Map PrayerRequest → PrayerResponse ───
    private PrayerResponse mapToResponse(
            PrayerRequest prayer) {

        Long count = prayerCountRepository
                .countByPrayerRequest(prayer);

        boolean isPraying = false;
        try {
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            if (email != null && !email.equals("anonymousUser")) {
                User user = userRepository
                        .findByEmail(email).orElse(null);
                if (user != null) {
                    isPraying = prayerCountRepository
                            .existsByUserAndPrayerRequest(
                                    user, prayer
                            );
                }
            }
        } catch (Exception ignored) {}

        return PrayerResponse.builder()
                .id(prayer.getId())
                .content(prayer.getContent())
                .isPublic(prayer.isPublic())
                .userName(prayer.getUser().getName())
                .churchName(prayer.getChurch() != null ?
                        prayer.getChurch().getName() : null)
                .prayerCount(count)
                .isPraying(isPraying)
                .createdAt(prayer.getCreatedAt())
                .build();
    }

    // 🙏 Submit prayer request
    public PrayerResponse submitPrayer(
            SubmitPrayerRequest request) {

        User currentUser = getCurrentUser();

        Church church = null;
        if (request.getChurchId() != null) {
            church = churchRepository
                    .findById(request.getChurchId())
                    .orElse(null);
        }

        PrayerRequest prayer = PrayerRequest.builder()
                .content(request.getContent())
                .isPublic(request.isPublic())
                .user(currentUser)
                .church(church)
                .build();

        prayerRepository.save(prayer);
        return mapToResponse(prayer);
    }

    // 🌐 Get all public prayers
    public List<PrayerResponse> getPublicPrayers() {
        return prayerRepository
                .findByIsPublicTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🙏 Toggle "I'm Praying" button
    public Map<String, Object> togglePraying(Long prayerId) {
        User currentUser = getCurrentUser();

        PrayerRequest prayer = prayerRepository
                .findById(prayerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Prayer request not found!"
                        )
                );

        if (prayerCountRepository.existsByUserAndPrayerRequest(
                currentUser, prayer)) {
            // Already praying → remove
            PrayerCount count = prayerCountRepository
                    .findByUserAndPrayerRequest(
                            currentUser, prayer
                    ).get();
            prayerCountRepository.delete(count);
            return Map.of(
                    "message", "Removed from praying list",
                    "isPraying", false,
                    "prayerCount", prayerCountRepository
                            .countByPrayerRequest(prayer)
            );
        } else {
            // Not praying → add
            PrayerCount count = PrayerCount.builder()
                    .user(currentUser)
                    .prayerRequest(prayer)
                    .build();
            prayerCountRepository.save(count);
            return Map.of(
                    "message", "🙏 Praying for this request!",
                    "isPraying", true,
                    "prayerCount", prayerCountRepository
                            .countByPrayerRequest(prayer)
            );
        }
    }

    // 📋 Get my prayer requests
    public List<PrayerResponse> getMyPrayers() {
        User currentUser = getCurrentUser();
        return prayerRepository
                .findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}