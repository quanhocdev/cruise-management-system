package com.project.convenience.service.service;

import com.project.convenience.dto.service.convenience.HistoryServiceTourResponse;
import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.mapper.HistoryServiceTourMapper;
import com.project.convenience.mapper.ServiceTourMapper;
import com.project.convenience.model.ServiceTour;
import com.project.convenience.model.enums.ServiceTourStatus;
import com.project.convenience.repository.HistoryServiceTourRepository;
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
        private final HistoryServiceTourRepository historyServiceTourRepository;

        public ServiceTourService(
                        ServiceTourRepository serviceTourRepository,
                        ServiceTourMapper serviceTourMapper,
                        HistoryServiceTourRepository historyServiceTourRepository) {

                this.serviceTourRepository = serviceTourRepository;
                this.serviceTourMapper = serviceTourMapper;
                this.historyServiceTourRepository = historyServiceTourRepository;
        }

        // =====================================================
        // CREATE FROM EVENT
        // =====================================================

        public void createServiceTourFromEvent(
                        UUID tourId,
                        UUID cruiseAreaId) {

                boolean exists = serviceTourRepository
                                .findByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)
                                .isPresent();

                if (exists) {
                        return;
                }

                ServiceTour serviceTour = new ServiceTour();

                serviceTour.setTourId(tourId);
                serviceTour.setCruiseAreaId(cruiseAreaId);
                serviceTour.setStatus(
                                ServiceTourStatus.WAITING_CONFIG);

                serviceTourRepository.save(serviceTour);
        }

        // =====================================================
        // DELETE FROM EVENT
        // =====================================================

        public void deleteServiceTourFromEvent(
                        UUID tourId,
                        UUID cruiseAreaId) {

                serviceTourRepository
                                .findByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)
                                .ifPresent(serviceTourRepository::delete);
        }

        // =====================================================
        // GET ALL
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
        // GET PENDING CONFIG
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

        // =====================================================
        // GET CONFIGURATION HISTORY
        // =====================================================

        @Transactional(readOnly = true)
        public List<HistoryServiceTourResponse> getConfigurationHistory() {

                return historyServiceTourRepository
                                .findAllByOrderByCompletedAtDesc()
                                .stream()
                                .map(HistoryServiceTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET CONFIGURATION HISTORY DETAIL
        // =====================================================

        @Transactional(readOnly = true)
        public List<ServiceTourResponse> getConfigurationHistoryDetail(
                        UUID tourId) {

                return serviceTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(serviceTourMapper::toResponse)
                                .toList();
        }
}