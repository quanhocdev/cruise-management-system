package com.project.tour.repository.service;

import com.project.tour.model.ServiceArea;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceAreaRepository
        extends JpaRepository<ServiceArea, UUID> {

    boolean existsByCruiseArea_IdAndService_Id(
            UUID areaId,
            UUID serviceId);

    boolean existsByCruiseArea_IdAndService_IdAndIdNot(
            UUID areaId,
            UUID serviceId,
            UUID excludedId);

    Optional<ServiceArea> findByIdAndCruiseArea_Id(
            UUID id,
            UUID areaId);

    List<ServiceArea> findAllByCruiseArea_IdOrderByService_NameAsc(
            UUID areaId);

    List<ServiceArea> findAllByService_IdOrderByCruiseArea_NameAsc(
            UUID serviceId);
}