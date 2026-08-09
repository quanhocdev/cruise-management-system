package com.project.tour.repository;

import com.project.tour.entity.Port;
import com.project.tour.entity.enums.PortStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PortRepository extends JpaRepository<Port, UUID>   {
    List<Port> findAllByStatusOrderByNameAsc(PortStatus status);
}
