package com.project.tour.repository.service;

import com.project.tour.model.Service;
import com.project.tour.model.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository
                extends JpaRepository<Service, UUID> {

        boolean existsByNameIgnoreCase(
                        String name);

        boolean existsByNameIgnoreCaseAndIdNot(
                        String name,
                        UUID excludedServiceId);

        Optional<Service> findById(
                        UUID serviceId);

        List<Service> findAllByOrderByNameAsc();

        List<Service> findAllByStatusOrderByNameAsc(
                        ServiceStatus status);
}