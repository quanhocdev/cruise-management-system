package com.project.booking.repository;

import com.project.booking.model.ProductUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUsageRepository
        extends JpaRepository<ProductUsage, Long> {

    List<ProductUsage> findAllByBookingPassenger_IdOrderByUsedAtDesc(
            Long bookingPassengerId);
}