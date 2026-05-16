package com.church.church_platform.service;

import com.church.church_platform.dto.request.SermonRequest;
import com.church.church_platform.dto.response.*;
import com.church.church_platform.entity.*;
import com.church.church_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SermonService {

    private final SermonRepository sermonRepository;
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

    // ─── Helper: Map Sermon to SermonResponse ──────────
    private SermonResponse mapToResponse(Sermon sermon) {
        return SermonResponse.builder()
                .id(sermon.getId())
                .title(sermon.getTitle())
                .description(sermon.getDescription())
                .videoUrl(sermon.getVideoUrl())
                .audioUrl(sermon.getAudioUrl())
                .thumbnailUrl(sermon.getThumbnailUrl())
                .speaker(sermon.getSpeaker())
                .series(sermon.getSeries())
                .churchId(sermon.getChurch().getId())
                .churchName(sermon.getChurch().getName())
                .churchSlug(sermon.getChurch().getSlug())
                .churchLogo(sermon.getChurch().getLogo())
                .createdAt(sermon.getCreatedAt())
                .build();
    }

    // 📤 Upload sermon (Church Admin)
    public SermonResponse uploadSermon(SermonRequest request) {
        Church church = getCurrentUserChurch();

        Sermon sermon = Sermon.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .videoUrl(request.getVideoUrl())
                .audioUrl(request.getAudioUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .speaker(request.getSpeaker())
                .series(request.getSeries())
                .church(church)
                .build();

        sermonRepository.save(sermon);
        return mapToResponse(sermon);
    }

    // 🌐 Get all sermons with pagination (Public)
    public PagedResponse<SermonResponse> getAllSermons(
            int page, int size) {

        // Create pageable object
        // page=0 means first page
        // size=10 means 10 sermons per page
        Pageable pageable = PageRequest.of(page, size);

        Page<Sermon> sermonPage = sermonRepository
                .findAllByOrderByCreatedAtDesc(pageable);

        List<SermonResponse> content = sermonPage
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<SermonResponse>builder()
                .content(content)
                .currentPage(page)
                .totalPages(sermonPage.getTotalPages())
                .totalElements(sermonPage.getTotalElements())
                .isLast(sermonPage.isLast())
                .build();
    }

    // 🔍 Get single sermon by id (Public)
    public SermonResponse getSermonById(Long id) {
        Sermon sermon = sermonRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Sermon not found!")
                );
        return mapToResponse(sermon);
    }

    // 🏛️ Get all sermons by church (Public)
    public List<SermonResponse> getSermonsByChurch(Long churchId) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() ->
                        new RuntimeException("Church not found!")
                );

        return sermonRepository
                .findByChurchOrderByCreatedAtDesc(church)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 👤 Get my church sermons (Church Admin)
    public List<SermonResponse> getMySermons() {
        Church church = getCurrentUserChurch();
        return sermonRepository
                .findByChurchOrderByCreatedAtDesc(church)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✏️ Update sermon (Church Admin)
    public SermonResponse updateSermon(
            Long id, SermonRequest request) {

        Church church = getCurrentUserChurch();

        Sermon sermon = sermonRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Sermon not found!")
                );

        // Make sure sermon belongs to this church
        if (!sermon.getChurch().getId()
                .equals(church.getId())) {
            throw new RuntimeException(
                    "You are not authorized to update this sermon!"
            );
        }

        // Update fields
        if (request.getTitle() != null)
            sermon.setTitle(request.getTitle());
        if (request.getDescription() != null)
            sermon.setDescription(request.getDescription());
        if (request.getVideoUrl() != null)
            sermon.setVideoUrl(request.getVideoUrl());
        if (request.getAudioUrl() != null)
            sermon.setAudioUrl(request.getAudioUrl());
        if (request.getThumbnailUrl() != null)
            sermon.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getSpeaker() != null)
            sermon.setSpeaker(request.getSpeaker());
        if (request.getSeries() != null)
            sermon.setSeries(request.getSeries());

        sermonRepository.save(sermon);
        return mapToResponse(sermon);
    }

    // 🗑️ Delete sermon (Church Admin)
    public void deleteSermon(Long id) {
        Church church = getCurrentUserChurch();

        Sermon sermon = sermonRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Sermon not found!")
                );

        // Make sure sermon belongs to this church
        if (!sermon.getChurch().getId()
                .equals(church.getId())) {
            throw new RuntimeException(
                    "You are not authorized to delete this sermon!"
            );
        }

        sermonRepository.delete(sermon);
    }

    // 🔍 Search sermons (Public)
    public List<SermonResponse> searchSermons(String keyword) {
        return sermonRepository.searchSermons(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}