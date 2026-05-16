package com.church.church_platform.repository;

import com.church.church_platform.entity.Church;
import com.church.church_platform.entity.Sermon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SermonRepository
        extends JpaRepository<Sermon, Long> {

    // Get all sermons by church
    List<Sermon> findByChurchOrderByCreatedAtDesc(Church church);

    // Get all sermons with pagination
    Page<Sermon> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Search sermons by title or speaker
    @Query("SELECT s FROM Sermon s WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(s.speaker) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(s.series) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    List<Sermon> searchSermons(@Param("keyword") String keyword);

    // Get sermons by series
    List<Sermon> findBySeriesIgnoreCaseOrderByCreatedAtDesc(
            String series
    );

    // Count sermons per church
    Long countByChurch(Church church);
}