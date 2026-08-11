package com.project.tour.repository;

import com.project.tour.model.PortCall;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortCallRepository extends JpaRepository<PortCall, UUID> {
    Optional<PortCall> findByIdAndItineraryDay_Id(UUID id, UUID dayId);
    List<PortCall> findAllByItineraryDay_IdOrderByPlannedArrivalTimeAsc(UUID dayId);
    boolean existsByItineraryDay_IdAndPort_Id(UUID dayId, UUID portId);
    boolean existsByItineraryDay_IdAndPort_IdAndIdNot(UUID dayId, UUID portId, UUID id);
}
