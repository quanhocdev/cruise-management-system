package com.project.tour.service.tour.operation;

import com.project.tour.dto.cruise.CruiseAvailabilityResponse;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.cruise.CruiseStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.cruise.CruiseRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourCruiseAssignmentService {

    private final TourRepository tourRepository;
    private final CruiseRepository cruiseRepository;

    public TourCruiseAssignmentService(TourRepository tourRepository, CruiseRepository cruiseRepository) {
        this.tourRepository = tourRepository;
        this.cruiseRepository = cruiseRepository;
    }

    /**
     * LẤY DANH SÁCH TẤT CẢ CRUISE KÈM TRẠNG THÁI KHẢ DỤNG CHO TOUR
     */
    @Transactional(readOnly = true)
    public List<CruiseAvailabilityResponse> getAvailableCruises(UUID tourId) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

        if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
            throw new AppException("Tour is not waiting for approval", HttpStatus.BAD_REQUEST);
        }

        List<Cruise> allCruises = cruiseRepository.findAll();

        return allCruises.stream().map(cruise -> {

            if (cruise.getStatus() != CruiseStatus.ACTIVE) {
                return new CruiseAvailabilityResponse(
                        cruise.getId(),
                        cruise.getCode(),
                        cruise.getName(),
                        cruise.getStatus(),
                        false,
                        "Du thuyền đang ở trạng thái: " + cruise.getStatus().name(),
                        List.of());
            }

            List<Tour> conflictingTours = findConflictingTours(
                    cruise.getId(),
                    tour.getStartDate(),
                    tour.getEndDate());

            List<CruiseAvailabilityResponse.ConflictingTourInfo> conflictInfos = conflictingTours.stream()
                    .filter(t -> !t.getId().equals(tourId))
                    .map(t -> new CruiseAvailabilityResponse.ConflictingTourInfo(
                            t.getId(),
                            t.getCode(),
                            t.getName(),
                            t.getStartDate(),
                            t.getEndDate()))
                    .toList();

            boolean isAvailable = conflictInfos.isEmpty();
            String reason = isAvailable
                    ? "Khả dụng"
                    : "Trùng lịch với " + conflictInfos.size() + " Tour khác";

            return new CruiseAvailabilityResponse(
                    cruise.getId(),
                    cruise.getCode(),
                    cruise.getName(),
                    cruise.getStatus(),
                    isAvailable,
                    reason,
                    conflictInfos);

        }).toList();
    }

    /**
     * GÁN DU THUYỀN CHO TOUR (Vẫn giữ PENDING)
     */
    public TourResponse assignCruise(UUID tourId, UUID cruiseId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

        if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
            throw new AppException("Tour is not waiting for approval", HttpStatus.BAD_REQUEST);
        }

        // ĐÃ BỔ SUNG: Kiểm tra chặn đè nếu Tour đã gán tàu trước đó
        if (tour.getCruise() != null) {
            throw new AppException(
                    "Tour has already been assigned a cruise. Please unassign first before selecting a new one.",
                    HttpStatus.BAD_REQUEST);
        }

        Cruise cruise = cruiseRepository.findById(cruiseId)
                .orElseThrow(() -> new AppException("Cruise not found", HttpStatus.NOT_FOUND));

        if (cruise.getStatus() != CruiseStatus.ACTIVE) {
            throw new AppException("Cruise is not active", HttpStatus.BAD_REQUEST);
        }

        List<Tour> conflictingTours = findConflictingTours(
                cruiseId,
                tour.getStartDate(),
                tour.getEndDate());

        boolean hasConflict = conflictingTours.stream()
                .anyMatch(t -> !t.getId().equals(tourId));

        if (hasConflict) {
            throw new AppException(
                    "Cruise is already assigned to another tour during this period",
                    HttpStatus.CONFLICT);
        }

        tour.setCruise(cruise);
        Tour savedTour = tourRepository.save(tour);

        return TourMapper.toResponse(savedTour);
    }

    /**
     * KIỂM TRA CRUISE CÓ BỊ TRÙNG LỊCH KHÔNG
     */
    private List<Tour> findConflictingTours(UUID cruiseId, LocalDate startDate, LocalDate endDate) {
        return tourRepository.findConflictingTours(
                cruiseId,
                List.of(TourStatusTrip.APPROVED, TourStatusTrip.IN_PROGRESS),
                startDate,
                endDate);
    }
}