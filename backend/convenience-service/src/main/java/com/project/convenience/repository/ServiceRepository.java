package com.project.convenience.repository;

import com.project.convenience.model.Service;
import com.project.convenience.model.enums.ServiceStatus;
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