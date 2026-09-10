package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_deviation_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDeviationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_session_id", nullable = false)
    private TrackingSession session;

    private Long readerId;
    private Long exitTime;
    private Long returnTime;
    private Double exitLatitude;
    private Double exitLongitude;
    private Double maximumDistanceMeters;
    private Long durationSeconds;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
