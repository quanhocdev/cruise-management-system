package com.project.booking.repository;
import com.project.booking.model.PassengerVoyage;
import com.project.booking.model.enums.PassengerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PassengerVoyageRepository extends JpaRepository<PassengerVoyage, Long> {
    boolean existsByVoyageId(UUID voyageId);
    List<PassengerVoyage> findAllByVoyageIdOrderByIdAsc(UUID voyageId);
    List<PassengerVoyage> findAllByBooking_IdOrderByIdAsc(Long bookingId);
    long countByVoyageIdAndPassengerStatusIn(UUID voyageId, Collection<PassengerStatus> statuses);
    long countByVoyageIdAndCabinIdAndPassengerStatusIn(
        UUID voyageId, UUID cabinId, Collection<PassengerStatus> statuses);
}
