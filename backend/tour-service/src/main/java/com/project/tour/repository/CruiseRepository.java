package com.project.tour.repository;

import com.project.tour.model.Cruise;
import com.project.tour.model.enums.CruiseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CruiseRepository
    extends JpaRepository<Cruise, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Cruise> findByCodeIgnoreCase(String code);

    List<Cruise> findAllByStatusOrderByNameAsc(
        CruiseStatus status
    );
}
