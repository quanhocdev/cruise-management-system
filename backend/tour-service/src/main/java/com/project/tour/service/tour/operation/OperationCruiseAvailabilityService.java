package com.project.tour.service.tour.operation;

import com.project.tour.dto.cruise.CruiseAvailabilityResponse;
import com.project.tour.exception.AppException;
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
@Transactional(readOnly = true)
public class OperationCruiseAvailabilityService {

    private final TourRepository tourRepository;
    private final CruiseRepository cruiseRepository;

    public OperationCruiseAvailabilityService(
            TourRepository tourRepository,
            CruiseRepository cruiseRepository) {

        this.tourRepository = tourRepository;
        this.cruiseRepository = cruiseRepository;
    }

    /**
     * Lấy danh sách Cruise và kiểm tra khả dụng cho Tour.
     */
    public List<CruiseAvailabilityResponse> getAvailableCruises(
            UUID tourId) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));

        if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
            throw new AppException(
                    "Tour is not waiting for approval",
                    HttpStatus.BAD_REQUEST);
        }

        List<Cruise> allCruises = cruiseRepository.findAll();

        return allCruises.stream()
                .map(cruise -> buildAvailabilityResponse(
                        cruise,
                        tour))
                .toList();
    }

    /**
     * Kiểm tra một Cruise có bị trùng lịch với Tour khác hay không.
     */
    private List<Tour> findConflictingTours(
            UUID cruiseId,
            LocalDate startDate,
            LocalDate endDate) {

        return tourRepository.findConflictingTours(
                cruiseId,
                List.of(
                        TourStatusTrip.APPROVED,
                        TourStatusTrip.IN_PROGRESS),
                startDate,
                endDate);
    }

    /**
     * Tạo response trạng thái khả dụng của một Cruise.
     */
    private CruiseAvailabilityResponse buildAvailabilityResponse(
            Cruise cruise,
            Tour tour) {

        // Cruise không ACTIVE
        if (cruise.getStatus() != CruiseStatus.ACTIVE) {

            return new CruiseAvailabilityResponse(
                    cruise.getId(),
                    cruise.getCode(),
                    cruise.getName(),
                    cruise.getStatus(),
                    false,
                    "Du thuyền đang ở trạng thái: "
                            + cruise.getStatus().name(),
                    List.of());
        }

        // Tìm các Tour bị trùng lịch
        List<Tour> conflictingTours = findConflictingTours(
                cruise.getId(),
                tour.getStartDate(),
                tour.getEndDate());

        List<CruiseAvailabilityResponse.ConflictingTourInfo> conflictInfos = conflictingTours.stream()
                .filter(t -> !t.getId().equals(tour.getId()))
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
                : "Trùng lịch với "
                        + conflictInfos.size()
                        + " Tour khác";

        return new CruiseAvailabilityResponse(
                cruise.getId(),
                cruise.getCode(),
                cruise.getName(),
                cruise.getStatus(),
                isAvailable,
                reason,
                conflictInfos);
    }
}