package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.TrackingSession;

@Repository
public interface TrackingSessionR extends JpaRepository<TrackingSession, String> {
    Optional<TrackingSession> findByIdAndReaderId(String id, Long readerId);
    List<TrackingSession> findByReaderIdOrderByStartTimeDesc(Long readerId);
    List<TrackingSession> findByWorkDateOrderByStartTimeDesc(LocalDate workDate);
    List<TrackingSession> findByReaderIdAndWorkDateOrderByStartTimeDesc(Long readerId, LocalDate workDate);
    List<TrackingSession> findAllByOrderByWorkDateDescStartTimeDesc();
}
