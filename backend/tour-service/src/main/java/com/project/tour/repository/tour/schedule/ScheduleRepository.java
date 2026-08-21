package com.project.tour.repository.tour.schedule;

import com.project.tour.model.Schedule;
import com.project.tour.model.enums.ScheduleStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

        boolean existsByTour_IdAndDayNumber(
                        UUID tourId,
                        Integer dayNumber);

        boolean existsByTour_IdAndDayNumberAndIdNot(
                        UUID tourId,
                        Integer dayNumber,
                        UUID excludedScheduleId);

        Optional<Schedule> findByIdAndTour_Id(
                        UUID id,
                        UUID tourId);

        List<Schedule> findAllByTour_IdOrderByDayNumberAsc(
                        UUID tourId);

        List<Schedule> findAllByTour_IdAndStatusOrderByDayNumberAsc(
                        UUID tourId,
                        ScheduleStatus status);

        Optional<Schedule> findFirstByTour_IdAndStatusOrderByRealDayAsc(
                        UUID tourId,
                        ScheduleStatus status);
}
