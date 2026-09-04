// package com.project.tour.repository;

// import com.project.tour.model.ItineraryDay;
// import org.springframework.data.jpa.repository.JpaRepository;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// public interface ItineraryDayRepository extends JpaRepository<ItineraryDay,
// UUID> {
// Optional<ItineraryDay> findByIdAndSchedule_Id(UUID id, UUID scheduleId);
// List<ItineraryDay> findAllBySchedule_IdOrderByDayNumberAsc(UUID scheduleId);
// boolean existsBySchedule_IdAndDayNumber(UUID scheduleId, Integer dayNumber);
// boolean existsBySchedule_IdAndItineraryDate(UUID scheduleId, LocalDate date);
// boolean existsBySchedule_IdAndDayNumberAndIdNot(UUID scheduleId, Integer
// dayNumber, UUID id);
// boolean existsBySchedule_IdAndItineraryDateAndIdNot(UUID scheduleId,
// LocalDate date, UUID id);
// }
