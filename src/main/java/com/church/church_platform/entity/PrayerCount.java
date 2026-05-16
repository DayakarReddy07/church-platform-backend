package com.church.church_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prayer_counts",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "prayer_request_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prayer_request_id")
    private PrayerRequest prayerRequest;

    private LocalDateTime prayedAt;

    @PrePersist
    public void prePersist() {
        prayedAt = LocalDateTime.now();
    }
}