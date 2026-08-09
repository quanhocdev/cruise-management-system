package com.project.tour.repository;

import com.project.tour.entity.Port;
import com.project.tour.entity.enums.PortStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortRepository extends JpaRepository<Port, UUID>   {
    Optional<Port> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    List<Port> findAllByStatusOrderByNameAsc(PortStatus status);
}