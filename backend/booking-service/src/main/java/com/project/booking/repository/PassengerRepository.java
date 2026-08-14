package com.project.booking.repository;
import com.project.booking.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PassengerRepository extends JpaRepository<Passenger, Long> {}
