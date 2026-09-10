package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "gps_gap_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsGapEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_session_id", nullable = false)
    private TrackingSession session;

    private Long startTime;
    private Long endTime;
    private Long durationSeconds;
    private Double lastLatitude;
    private Double lastLongitude;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
