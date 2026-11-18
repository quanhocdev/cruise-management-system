
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.route.PassengerTourRouteDTO;
import com.project.tour.mapper.passenger.TourRouteMapper;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.scheduler.TourRouteLegRepository;
import com.project.tour.repository.scheduler.TourRouteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class PassengerRouteService {

    private final TourRepository tourRepository;
    private final TourRouteRepository tourRouteRepository;
    private final TourRouteLegRepository tourRouteLegRepository;
    private final TourRouteMapper tourRouteMapper;

    public PassengerRouteService(
            TourRepository tourRepository,
            TourRouteRepository tourRouteRepository,
            TourRouteLegRepository tourRouteLegRepository,
            TourRouteMapper tourRouteMapper) {
        this.tourRepository = tourRepository;
        this.tourRouteRepository = tourRouteRepository;
        this.tourRouteLegRepository = tourRouteLegRepository;
        this.tourRouteMapper = tourRouteMapper;
    }

    public PassengerTourRouteDTO getRoute(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        TourRoute route = tourRouteRepository.findByTour_Id(tourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tour chưa có thông tin tuyến đường"));

        var legs = tourRouteLegRepository
                .findAllByTourRoute_IdOrderByLegOrderAsc(route.getId());

        return tourRouteMapper.toDTO(route, legs);
    }
}
