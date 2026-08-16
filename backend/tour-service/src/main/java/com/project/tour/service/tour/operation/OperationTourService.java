package com.project.tour.service.tour.operation;

import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseMapper;
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
public class OperationTourService {

    private final TourRepository tourRepository;
    private final CruiseRepository cruiseRepository;

    public OperationTourService(
            TourRepository tourRepository,
            CruiseRepository cruiseRepository) {

        this.tourRepository = tourRepository;
        this.cruiseRepository = cruiseRepository;
    }

    /**
     * =====================================================
     * GET TOURS CHỜ DUYỆT
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<TourResponse> getPendingTours() {

        return tourRepository
                .findAllByStatusTripOrderByNameAsc(
                        TourStatusTrip.APPROVAL_PENDING)
                .stream()
                .map(TourMapper::toResponse)
                .toList();
    }

    /**
     * =====================================================
     * GET CRUISE KHẢ DỤNG CHO TOUR
     * =====================================================
     *
     * Chỉ lấy Cruise:
     *
     * - ACTIVE
     * - Không bị Tour APPROVED giữ
     * - Không bị Tour IN_PROGRESS giữ
     *
     * Tour APPROVAL_PENDING không được tính là đang giữ Cruise.
     */
    @Transactional(readOnly = true)
    public List<CruiseResponse> getAvailableCruises(UUID tourId) {

        Tour tour = tourRepository
                .findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));

        if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
            throw new AppException(
                    "Tour is not waiting for approval",
                    HttpStatus.BAD_REQUEST);
        }

        return cruiseRepository
                .findAvailableCruises(
                        CruiseStatus.ACTIVE,
                        List.of(
                                TourStatusTrip.APPROVED,
                                TourStatusTrip.IN_PROGRESS),
                        tour.getStartDate(),
                        tour.getEndDate())
                .stream()
                .map(CruiseMapper::toResponse)
                .toList();
    }

    /**
     * =====================================================
     * KIỂM TRA CRUISE CÓ BỊ TRÙNG LỊCH KHÔNG
     * =====================================================
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

    public TourResponse approveTour(
            UUID tourId,
            UUID cruiseId) {

        Tour tour = tourRepository
                .findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));

        // =====================================================
        // 2. TOUR PHẢI ĐANG CHỜ DUYỆT
        // =====================================================

        if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {

            throw new AppException(
                    "Tour is not waiting for approval",
                    HttpStatus.BAD_REQUEST);
        }

        // =====================================================
        // 3. TÌM CRUISE
        // =====================================================

        Cruise cruise = cruiseRepository
                .findById(cruiseId)
                .orElseThrow(() -> new AppException(
                        "Cruise not found",
                        HttpStatus.NOT_FOUND));

        if (cruise.getStatus() != CruiseStatus.ACTIVE) {

            throw new AppException(
                    "Cruise is not active",
                    HttpStatus.BAD_REQUEST);
        }

        // 5. KIỂM TRA CRUISE CÓ BỊ TRÙNG LỊCH KHÔNG
        List<Tour> conflictingTours = findConflictingTours(
                cruiseId,
                tour.getStartDate(),
                tour.getEndDate());

        if (!conflictingTours.isEmpty()) {

            throw new AppException(
                    "Cruise is already assigned to another tour during this period",
                    HttpStatus.CONFLICT);
        }

        tour.setCruise(cruise);

        tour.setStatusTrip(
                TourStatusTrip.APPROVED);

        Tour savedTour = tourRepository.save(tour);

        return TourMapper.toResponse(savedTour);
    }
}