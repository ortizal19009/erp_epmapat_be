package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracking_points", uniqueConstraints = @UniqueConstraint(name = "uk_tracking_point_client", columnNames = "client_point_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_point_id", length = 100, unique = true)
    private String clientPointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_session_id", nullable = false)
    @JsonIgnore
    private TrackingSession session;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Double accuracy;
    private Double altitude;
    private Double speed;
    private Double bearing;

    private Integer batteryLevel;

    @Builder.Default
    private Boolean isMockLocation = false;

    @Column(length = 20)
    private String routeStatus;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
