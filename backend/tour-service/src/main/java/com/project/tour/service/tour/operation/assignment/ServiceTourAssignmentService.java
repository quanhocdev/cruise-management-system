package com.project.tour.service.tour.operation.assignment;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.model.convenience.service.ServiceTour;
import com.project.tour.repository.convenience.ServiceTourRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceTourAssignmentService {

        private final ServiceTourRepository serviceTourRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ServiceTourAssignmentMapper assignmentMapper;

        public ServiceTourAssignmentService(
                        ServiceTourRepository serviceTourRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ServiceTourAssignmentMapper assignmentMapper) {

                this.serviceTourRepository = serviceTourRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.assignmentMapper = assignmentMapper;
        }

        public ServiceTourAssignmentResponse assign(
                        ServiceTourAssignmentRequest request) {

                // =====================================================
                // 1. Kiểm tra Tour
                // =====================================================

                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 2. Kiểm tra CruiseArea
                // =====================================================

                CruiseArea cruiseArea = cruiseAreaRepository.findById(
                                request.cruiseAreaId())
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));

                // =====================================================
                // 3. Validate CruiseArea
                // =====================================================

                if (cruiseArea.getStatus() == null) {
                        throw new AppException(
                                        "Cruise area status is invalid",
                                        HttpStatus.BAD_REQUEST);
                }

                if (cruiseArea.getCruiseDeck() == null) {
                        throw new AppException(
                                        "Cruise area is not assigned to a deck",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 4. Tour phải được gán Cruise
                // =====================================================

                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has not been assigned to a cruise",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 5. CruiseArea phải thuộc cùng Cruise với Tour
                // =====================================================

                if (!tour.getCruise().getId()
                                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // =====================================================
                // 6. Chống phân công trùng
                // =====================================================

                ServiceTour serviceTour = serviceTourRepository
                                .findByTourIdAndCruiseAreaId(
                                                request.tourId(),
                                                request.cruiseAreaId())
                                .orElseGet(() -> {

                                        ServiceTour newServiceTour = new ServiceTour();

                                        newServiceTour.setTourId(
                                                        request.tourId());

                                        newServiceTour.setCruiseAreaId(
                                                        request.cruiseAreaId());

                                        // Entity mặc định WAITING_CONFIG.
                                        return serviceTourRepository.save(
                                                        newServiceTour);
                                });

                // =====================================================
                // 7. Trả response
                // =====================================================

                return assignmentMapper.toResponse(
                                serviceTour,
                                tour,
                                cruiseArea);
        }

        /**
         * Lấy toàn bộ phân công Service của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ServiceTourAssignmentResponse> getByTour(
                        UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return serviceTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(serviceTour -> {

                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(serviceTour.getCruiseAreaId())
                                                        .orElse(null);

                                        return assignmentMapper.toResponse(
                                                        serviceTour,
                                                        tour,
                                                        cruiseArea);
                                })
                                .toList();
        }

        /**
         * Xóa phân công Service.
         *
         * Chỉ xóa ServiceTour trong DB của tour-service.
         * Không bắn Kafka.
         */
        @Transactional
        public void deleteAssignment(
                        UUID tourId,
                        UUID cruiseAreaId) {

                if (!serviceTourRepository
                                .existsByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)) {

                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                serviceTourRepository.deleteByTourIdAndCruiseAreaId(
                                tourId,
                                cruiseAreaId);
        }
}