package com.project.tour.repository;

import com.project.tour.model.Schedule;
import com.project.tour.model.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    boolean existsByCodeIgnoreCase(String code);
    Optional<Schedule> findByCodeIgnoreCase(String code);
    List<Schedule> findAllByOrderByStartDateAsc();
    List<Schedule> findAllByStatusOrderByStartDateAsc(ScheduleStatus status);
    List<Schedule> findAllByStartDateGreaterThanEqualOrderByStartDateAsc(LocalDate date);

    @Query("""
        select (count(s) > 0) from Schedule s
        where s.cruise.id = :cruiseId
          and s.status <> com.project.tour.model.enums.ScheduleStatus.CANCELLED
          and s.startDate <= :endDate and s.endDate >= :startDate
          and (:excludedId is null or s.id <> :excludedId)
        """)
    boolean hasCruiseDateConflict(
        @Param("cruiseId") UUID cruiseId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("excludedId") UUID excludedId
    );
}
