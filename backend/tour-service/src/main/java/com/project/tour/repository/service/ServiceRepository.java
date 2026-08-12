package com.project.tour.repository.service;

import com.project.tour.model.Service;
import com.project.tour.model.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {

    boolean existsByCruiseArea_IdAndNameIgnoreCase(
            UUID areaId,
            String name);

    boolean existsByCruiseArea_IdAndNameIgnoreCaseAndIdNot(
            UUID areaId,
            String name,
            UUID excludedServiceId);

    Optional<Service> findByIdAndCruiseArea_Id(
            UUID id,
            UUID areaId);

    List<Service> findAllByCruiseArea_IdOrderByNameAsc(
            UUID areaId);

    List<Service> findAllByCruiseArea_IdAndStatusOrderByNameAsc(
            UUID areaId,
            ServiceStatus status);
}