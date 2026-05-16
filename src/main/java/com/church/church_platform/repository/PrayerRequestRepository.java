package com.church.church_platform.repository;

import com.church.church_platform.entity.PrayerRequest;
import com.church.church_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrayerRequestRepository
        extends JpaRepository<PrayerRequest, Long> {

    List<PrayerRequest> findByIsPublicTrueOrderByCreatedAtDesc();

    List<PrayerRequest> findByUserOrderByCreatedAtDesc(User user);
}