package com.church.church_platform.repository;

import com.church.church_platform.entity.Church;
import com.church.church_platform.entity.Follow;
import com.church.church_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Count followers for a church
    Long countByChurch(Church church);

    // Check if user already follows a church
    boolean existsByUserAndChurch(User user, Church church);

    // Find follow record
    Optional<Follow> findByUserAndChurch(User user, Church church);

    // Get all churches a user follows
    List<Follow> findByUser(User user);

    // Get all followers of a church
    List<Follow> findByChurch(Church church);
}