package com.project.booking.repository;

import com.project.booking.model.PassengerVoyage;
import com.project.booking.model.enums.PassengerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface PassengerVoyageRepository extends JpaRepository<PassengerVoyage, Long> {
    boolean existsByVoyageId(UUID voyageId);
    List<PassengerVoyage> findAllByVoyageIdOrderByIdAsc(UUID voyageId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pv from PassengerVoyage pv where pv.id = :id")
    Optional<PassengerVoyage> findByIdForUpdate(@Param("id") Long id);
    List<PassengerVoyage> findAllByBooking_IdOrderByIdAsc(Long bookingId);
    
    long countByVoyageIdAndPassengerStatusIn(UUID voyageId, Collection<PassengerStatus> statuses);
    
    long countByVoyageIdAndCabinIdAndPassengerStatusIn(
        UUID voyageId, UUID cabinId, Collection<PassengerStatus> statuses);
        
    Optional<PassengerVoyage> findByNfcTagIdIgnoreCase(String nfcTagId);
    
    boolean existsByNfcTagIdIgnoreCase(String nfcTagId);
    
    Optional<PassengerVoyage> findFirstByBooking_IdAndPassenger_UserId(Long bookingId, Long userId);
}