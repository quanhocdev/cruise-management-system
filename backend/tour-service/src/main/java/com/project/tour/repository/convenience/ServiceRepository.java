package com.project.tour.repository.convenience;

import com.project.tour.model.convenience.enums.ServiceStatus;
import com.project.tour.model.convenience.service.Service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository
        extends JpaRepository<Service, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            UUID excludedServiceId);

    Optional<Service> findById(UUID serviceId);

    List<Service> findAllByOrderByNameAsc();

    List<Service> findAllByStatusOrderByNameAsc(
            ServiceStatus status);
}