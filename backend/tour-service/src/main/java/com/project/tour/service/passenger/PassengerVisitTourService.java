
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.activity.PassengerVisitTourDTO;
import com.project.tour.mapper.passenger.VisitTourMapper;
import com.project.tour.model.shore.VisitTour;
import com.project.tour.model.shore.enums.VisitTourStatus;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.shore.VisitTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerVisitTourService {

    private static final Set<VisitTourStatus> PASSENGER_VISIBLE_STATUSES = Set.of(
            VisitTourStatus.CONFIGURED,
            VisitTourStatus.NOT_STARTED,
            VisitTourStatus.IN_PROGRESS,
            VisitTourStatus.COMPLETED,
            VisitTourStatus.DELAYED,
            VisitTourStatus.CANCELLED);

    private final TourRepository tourRepository;
    private final VisitTourRepository visitTourRepository;
    private final VisitTourMapper visitTourMapper;

    public PassengerVisitTourService(
            TourRepository tourRepository,
            VisitTourRepository visitTourRepository,
            VisitTourMapper visitTourMapper) {
        this.tourRepository = tourRepository;
        this.visitTourRepository = visitTourRepository;
        this.visitTourMapper = visitTourMapper;
    }

    public List<PassengerVisitTourDTO> getVisits(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        List<VisitTour> visits = visitTourRepository.findAllByTourIdOrderByStartTimeAsc(tourId);

        return visits.stream()
                .filter(item -> PASSENGER_VISIBLE_STATUSES.contains(item.getStatus()))
                .map(visitTourMapper::toDTO)
                .toList();
    }
}
