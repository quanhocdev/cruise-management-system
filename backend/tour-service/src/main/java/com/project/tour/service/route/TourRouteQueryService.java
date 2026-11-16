package com.project.tour.service.route;

import com.project.tour.dto.route.TourRouteLegResponse;
import com.project.tour.dto.route.TourRouteOverviewResponse;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.model.scheduler.TourRouteLeg;
import com.project.tour.repository.scheduler.TourRouteLegRepository;
import com.project.tour.repository.scheduler.TourRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TourRouteQueryService {

        private final TourRouteRepository tourRouteRepository;
        private final TourRouteLegRepository tourRouteLegRepository;

        public TourRouteQueryService(TourRouteRepository tourRouteRepository,
                        TourRouteLegRepository tourRouteLegRepository) {
                this.tourRouteRepository = tourRouteRepository;
                this.tourRouteLegRepository = tourRouteLegRepository;
        }

        // Lấy tổng quan lộ trình từ bảng chính (đầy đủ các trường)
        public TourRouteOverviewResponse getOverviewByTourId(UUID tourId) {
                TourRoute route = tourRouteRepository.findByTour_Id(tourId)
                                .orElseThrow(() -> new RuntimeException("Route not found for tour ID: " + tourId));

                return new TourRouteOverviewResponse(
                                tourId,
                                route.getDeparturePort() != null ? route.getDeparturePort().getId() : null,
                                route.getDeparturePort() != null ? route.getDeparturePort().getName() : null,
                                route.getDestinationPort() != null ? route.getDestinationPort().getId() : null,
                                route.getDestinationPort() != null ? route.getDestinationPort().getName() : null,
                                route.getDistanceKm(),
                                route.getDistanceFromDepartureKm(),
                                route.getDistanceToDestinationKm(),
                                route.getRouteGeoJson(),
                                route.getCreatedAt(),
                                route.getUpdatedAt());
        }

        // Lấy chi tiết danh sách các chặng từ bảng phụ
        public List<TourRouteLegResponse> getLegsByTourId(UUID tourId) {
                TourRoute route = tourRouteRepository.findByTour_Id(tourId)
                                .orElseThrow(() -> new RuntimeException("Route not found for tour ID: " + tourId));

                List<TourRouteLeg> legs = tourRouteLegRepository.findAllByTourRoute_IdOrderByLegOrderAsc(route.getId());

                return legs.stream().map(leg -> new TourRouteLegResponse(
                                leg.getId(),
                                leg.getLegOrder(),
                                leg.getFromPort() != null ? leg.getFromPort().getId() : null,
                                leg.getFromPort() != null ? leg.getFromPort().getName() : null,
                                leg.getToPort() != null ? leg.getToPort().getId() : null,
                                leg.getToPort() != null ? leg.getToPort().getName() : null,
                                leg.getDistanceKm(),
                                leg.getRouteGeoJson(),
                                leg.getCreatedAt(),
                                leg.getUpdatedAt())).collect(Collectors.toList());
        }
}