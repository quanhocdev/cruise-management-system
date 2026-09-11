package com.project.tour.repository.tour;

import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {
    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID excludedTourId);

    Optional<Tour> findByCodeIgnoreCase(String code);

    List<Tour> findAllByOrderByNameAsc();

    List<Tour> findAllByCruise_IdOrderByNameAsc(UUID cruiseId);

    List<Tour> findAllByStatusTripOrderByNameAsc(TourStatusTrip statusTrip);

    List<Tour> findAllByStatusTripInOrderByStartDateAsc(List<TourStatusTrip> statuses);

    List<Tour> findAllByStatusBookingOrderByNameAsc(TourBookingStatus statusBooking);

    List<Tour> findAllByCruise_IdAndStatusTripOrderByNameAsc(UUID cruiseId, TourStatusTrip statusTrip);

    Optional<Tour> findByIdAndStatusTripIn(UUID id, List<TourStatusTrip> statuses);

    List<Tour> findByStatusTripIn(List<TourStatusTrip> statuses);

    @Query("""
            SELECT t FROM Tour t
            JOIN FETCH t.cruise c
            WHERE t.statusBooking = :bookingStatus
              AND t.statusTrip IN :tripStatuses
              AND (t.bookingStart IS NULL OR t.bookingStart <= :now)
              AND (t.bookingEnd IS NULL OR t.bookingEnd >= :now)
              AND t.endDate >= :today
            ORDER BY t.startDate ASC, t.name ASC
            """)
    List<Tour> findOpenForPassenger(
            @Param("bookingStatus") TourBookingStatus bookingStatus,
            @Param("tripStatuses") List<TourStatusTrip> tripStatuses,
            @Param("now") LocalDateTime now,
            @Param("today") LocalDate today);

    @Query("""
            SELECT t FROM Tour t
            JOIN FETCH t.cruise c
            WHERE t.id = :id
              AND t.statusBooking = :bookingStatus
              AND t.statusTrip IN :tripStatuses
              AND (t.bookingStart IS NULL OR t.bookingStart <= :now)
              AND (t.bookingEnd IS NULL OR t.bookingEnd >= :now)
              AND t.endDate >= :today
            """)
    Optional<Tour> findOpenForPassengerById(
            @Param("id") UUID id,
            @Param("bookingStatus") TourBookingStatus bookingStatus,
            @Param("tripStatuses") List<TourStatusTrip> tripStatuses,
            @Param("now") LocalDateTime now,
            @Param("today") LocalDate today);

    @Query("""
            SELECT t FROM Tour t
            WHERE t.cruise.id = :cruiseId
              AND t.statusTrip IN :statuses
              AND t.startDate <= :endDate
              AND t.endDate >= :startDate
            """)
    List<Tour> findConflictingTours(
            @Param("cruiseId") UUID cruiseId,
            @Param("statuses") List<TourStatusTrip> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
