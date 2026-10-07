package com.project.booking.repository;

import com.project.booking.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findAllByBooking_IdOrderByCreatedAtDesc(Long bookingId);

    Optional<Bill> findByBillCode(String billCode);

    boolean existsByBillCode(String billCode);
}