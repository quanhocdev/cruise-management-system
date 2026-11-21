
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.service.PassengerServiceDTO;
import com.project.tour.mapper.passenger.ServiceTourMapper;
import com.project.tour.model.convenience.enums.ServiceTourStatus;
import com.project.tour.model.convenience.service.ServiceTour;
import com.project.tour.repository.convenience.ServiceTourRepository;
import com.project.tour.repository.scheduler.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class PassengerServiceService {

    private static final Set<ServiceTourStatus> PASSENGER_VISIBLE_STATUSES = Set.of(
            ServiceTourStatus.CONFIGURED,
            ServiceTourStatus.NOT_STARTED,
            ServiceTourStatus.IN_PROGRESS,
            ServiceTourStatus.COMPLETED);

    private final TourRepository tourRepository;
    private final ServiceTourRepository serviceTourRepository;
    private final ServiceTourMapper serviceTourMapper;

    public PassengerServiceService(
            TourRepository tourRepository,
            ServiceTourRepository serviceTourRepository,
            ServiceTourMapper serviceTourMapper) {
        this.tourRepository = tourRepository;
        this.serviceTourRepository = serviceTourRepository;
        this.serviceTourMapper = serviceTourMapper;
    }

    public List<PassengerServiceDTO> getServices(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        List<ServiceTour> serviceTours = serviceTourRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);

        return serviceTours.stream()
                .filter(item -> PASSENGER_VISIBLE_STATUSES.contains(item.getStatus()))
                .map(serviceTourMapper::toDTO)
                .toList();
    }
}
