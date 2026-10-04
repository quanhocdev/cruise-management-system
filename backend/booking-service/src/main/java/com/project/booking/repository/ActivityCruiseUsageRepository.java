package com.project.booking.repository;

import com.project.booking.model.ActivityCruiseUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityCruiseUsageRepository
        extends JpaRepository<ActivityCruiseUsage, Long> {

    List<ActivityCruiseUsage> findByBookingPassengerId(
            Long bookingPassengerId);
}