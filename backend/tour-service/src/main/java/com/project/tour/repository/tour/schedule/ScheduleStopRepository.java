package com.project.tour.repository.tour.schedule;

import com.project.tour.model.ScheduleStop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleStopRepository
        extends JpaRepository<ScheduleStop, UUID> {

    boolean existsBySchedule_IdAndStopOrder(
            UUID scheduleId,
            Integer stopOrder);

    boolean existsBySchedule_IdAndStopOrderAndIdNot(
            UUID scheduleId,
            Integer stopOrder,
            UUID excludedStopId);

    Optional<ScheduleStop> findByIdAndSchedule_Id(
            UUID id,
            UUID scheduleId);

    List<ScheduleStop> findAllBySchedule_IdOrderByStopOrderAsc(
            UUID scheduleId);
}