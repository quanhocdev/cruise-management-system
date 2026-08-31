package com.project.convenience.service.service;

import com.project.common.event.ServiceTourConfiguredEvent;
import com.project.convenience.exception.AppException;
import com.project.convenience.model.HistoryServiceTour;
import com.project.convenience.model.Service;
import com.project.convenience.model.ServiceTour;
import com.project.convenience.model.enums.ServiceTourStatus;
import com.project.convenience.repository.HistoryServiceTourRepository;
import com.project.convenience.repository.ServiceTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class ServiceTourConfigurationService {

    private static final String SERVICE_TOUR_CONFIGURED_TOPIC = "service-tour-configured-topic";

    private final ServiceTourRepository serviceTourRepository;
    private final HistoryServiceTourRepository historyServiceTourRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ServiceTourConfigurationService(
            ServiceTourRepository serviceTourRepository,
            HistoryServiceTourRepository historyServiceTourRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.serviceTourRepository = serviceTourRepository;
        this.historyServiceTourRepository = historyServiceTourRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void completeConfiguration(UUID tourId) {

        // =====================================================
        // 1. KIỂM TRA ĐÃ HOÀN THÀNH CHƯA
        // =====================================================

        if (historyServiceTourRepository.existsByTourId(tourId)) {

            throw new AppException(
                    "Service tour configuration has already been completed",
                    HttpStatus.BAD_REQUEST);
        }

        // =====================================================
        // 2. LẤY TẤT CẢ SERVICE TOUR
        // =====================================================

        List<ServiceTour> serviceTours = serviceTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId);

        if (serviceTours.isEmpty()) {

            throw new AppException(
                    "No service tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // 3. KIỂM TRA TẤT CẢ ĐÃ CONFIGURED
        // =====================================================

        for (ServiceTour serviceTour : serviceTours) {

            if (serviceTour.getStatus() != ServiceTourStatus.CONFIGURED) {

                throw new AppException(
                        "All service tours must be CONFIGURED before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }

            if (serviceTour.getService() == null) {

                throw new AppException(
                        "Service configuration is missing",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // =====================================================
        // 4. LƯU HISTORY
        // =====================================================

        HistoryServiceTour history = new HistoryServiceTour();

        history.setTourId(tourId);

        history.setTotalConfigurations(
                serviceTours.size());

        historyServiceTourRepository.save(history);

        // =====================================================
        // 5. GỬI KAFKA
        // =====================================================

        for (ServiceTour serviceTour : serviceTours) {

            Service service = serviceTour.getService();

            ServiceTourConfiguredEvent event = new ServiceTourConfiguredEvent(
                    serviceTour.getId(),
                    serviceTour.getTourId(),
                    serviceTour.getCruiseAreaId(),

                    service.getId(),
                    service.getName(),
                    service.getDescription(),
                    service.getPrice(),

                    serviceTour.getMaxPassengers(),
                    serviceTour.getDurationMinutes(),

                    service.getImageUrl(),

                    serviceTour.getStatus().name(),

                    LocalDateTime.now());

            kafkaTemplate.send(
                    SERVICE_TOUR_CONFIGURED_TOPIC,
                    tourId.toString(),
                    event);
        }
    }
}