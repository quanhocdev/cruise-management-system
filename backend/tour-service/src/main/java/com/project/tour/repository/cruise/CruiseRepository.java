package com.project.tour.repository.cruise;

import com.project.tour.model.Cruise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CruiseRepository extends JpaRepository<Cruise, UUID> {

        boolean existsByCodeIgnoreCase(String code);

        boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}