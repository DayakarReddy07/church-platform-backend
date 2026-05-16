package com.church.church_platform.repository;

import com.church.church_platform.entity.Church;
import com.church.church_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChurchRepository extends JpaRepository<Church, Long> {

    // Find church by slug (unique name in URL)
    Optional<Church> findBySlug(String slug);

    // Check if slug already exists
    boolean existsBySlug(String slug);

    // Find church by admin (each admin has one church)
    Optional<Church> findByAdmin(User admin);

    // Find all verified churches
    List<Church> findByVerifiedTrue();

    // Search churches by name or city
    List<Church> findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(
            String name, String city
    );
}