package com.church.church_platform.repository;

import com.church.church_platform.entity.PrayerCount;
import com.church.church_platform.entity.PrayerRequest;
import com.church.church_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrayerCountRepository
        extends JpaRepository<PrayerCount, Long> {

    boolean existsByUserAndPrayerRequest(
            User user, PrayerRequest prayerRequest
    );

    Optional<PrayerCount> findByUserAndPrayerRequest(
            User user, PrayerRequest prayerRequest
    );

    Long countByPrayerRequest(PrayerRequest prayerRequest);
}