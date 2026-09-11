package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracking_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingSession {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false)
    private Long readerId;

    private Long routeId;

    @Column(length = 100)
    private String deviceId;

    @Column(nullable = false)
    private LocalDate workDate;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(length = 20)
    @Builder.Default
    private String status = "STARTED";

    @Builder.Default
    private Double totalDistanceMeters = 0.0;

    @Builder.Default
    private Integer totalPoints = 0;

    @Builder.Default
    private Integer totalReadings = 0;

    private Integer startBatteryLevel;

    private Integer endBatteryLevel;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TrackingPoint> points;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
