package com.project.convenience.service.service;

import com.project.common.event.ServiceTourConfiguredEvent;
import com.project.convenience.model.Service;
import com.project.convenience.model.ServiceTour;
import com.project.convenience.model.enums.ServiceTourStatus;
import com.project.convenience.repository.ServiceRepository;
import com.project.convenience.repository.ServiceTourRepository;

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
    private final ServiceRepository serviceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ServiceTourConfigurationService(
            ServiceTourRepository serviceTourRepository,
            ServiceRepository serviceRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.serviceTourRepository = serviceTourRepository;
        this.serviceRepository = serviceRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void completeConfiguration(UUID tourId) {

        List<ServiceTour> serviceTours = serviceTourRepository
                .findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        tourId,
                        ServiceTourStatus.CONFIGURED);

        if (serviceTours.isEmpty()) {
            throw new IllegalStateException(
                    "No configured service tours found for tour");
        }

        for (ServiceTour serviceTour : serviceTours) {

            Service service = serviceTour.getService();

            if (service == null) {
                throw new IllegalStateException(
                        "Service is not configured for serviceTourId="
                                + serviceTour.getId());
            }

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
                    serviceTour.getTourId().toString(),
                    event);
        }
    }
}