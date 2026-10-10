package com.project.booking.repository;

import com.project.booking.model.ActivityCruiseUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityCruiseUsageRepository
                extends JpaRepository<ActivityCruiseUsage, Long> {

        List<ActivityCruiseUsage> findByBookingPassengerId(Long bookingPassengerId);

        List<ActivityCruiseUsage> findAllByBookingPassengerIdOrderByUsedAtDesc(
                        Long bookingPassengerId);

        long countByActivityCruiseTourId(UUID activityCruiseTourId);

        List<ActivityCruiseUsage> findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                        Long userId);

        List<ActivityCruiseUsage> findAllByOrderByUsedAtDesc();
}