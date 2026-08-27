package com.project.convenience.service.service;

import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.mapper.ServiceTourMapper;
import com.project.convenience.model.ServiceTour;
import com.project.convenience.model.enums.ServiceTourStatus;
import com.project.convenience.repository.ServiceTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceTourService {

        private final ServiceTourRepository serviceTourRepository;
        private final ServiceTourMapper serviceTourMapper;

        public ServiceTourService(
                        ServiceTourRepository serviceTourRepository,
                        ServiceTourMapper serviceTourMapper) {

                this.serviceTourRepository = serviceTourRepository;
                this.serviceTourMapper = serviceTourMapper;
        }

        public void createServiceTourFromEvent(UUID tourId, UUID cruiseAreaId) {
                boolean exists = serviceTourRepository.findByTourIdAndCruiseAreaId(tourId, cruiseAreaId).isPresent();
                if (exists) {
                        return;
                }

                ServiceTour serviceTour = new ServiceTour();
                serviceTour.setTourId(tourId);
                serviceTour.setCruiseAreaId(cruiseAreaId);
                serviceTour.setStatus(ServiceTourStatus.WAITING_CONFIG);

                serviceTourRepository.save(serviceTour);
        }

        public void deleteServiceTourFromEvent(UUID tourId, UUID cruiseAreaId) {
                serviceTourRepository.findByTourIdAndCruiseAreaId(tourId, cruiseAreaId)
                                .ifPresent(serviceTourRepository::delete);
        }

        // =====================================================
        // GET ALL SERVICE TOURS
        // =====================================================

        @Transactional(readOnly = true)
        public List<ServiceTourResponse> getAllAssignments() {

                return serviceTourRepository
                                .findAll()
                                .stream()
                                .map(serviceTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET CONFIGURABLE SERVICES
        // =====================================================

        @Transactional(readOnly = true)
        public List<ServiceTourResponse> getPendingConfig() {

                return serviceTourRepository
                                .findConfigurable(
                                                List.of(ServiceTourStatus.WAITING_CONFIG))
                                .stream()
                                .map(serviceTourMapper::toResponse)
                                .toList();
        }
}