package com.project.tour.service.tour.operation;

import com.project.tour.dto.cruise.CruiseAvailabilityResponse;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.operation.OperationCruiseAreaResponse;
import com.project.tour.dto.tour.operation.OperationCruiseLayoutResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.cruise.CruiseAreaStatus;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;
import com.project.tour.model.enums.cruise.CruiseStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.cruise.CruiseDeckRepository;
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
        private final CruiseDeckRepository cruiseDeckRepository;
        private final CruiseAreaRepository cruiseAreaRepository;

        public OperationTourService(
                        TourRepository tourRepository,
                        CruiseRepository cruiseRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        CruiseAreaRepository cruiseAreaRepository) {

                this.tourRepository = tourRepository;
                this.cruiseRepository = cruiseRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
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
         * LẤY DANH SÁCH TẤT CẢ CRUISE KÈM TRẠNG THÁI KHẢ DỤNG CHO TOUR
         * =====================================================
         */
        @Transactional(readOnly = true)
        public List<CruiseAvailabilityResponse> getAvailableCruises(UUID tourId) {

                // 1. Kiểm tra Tour tồn tại & trạng thái
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

                // 2. Lấy tất cả du thuyền trong hệ thống
                List<Cruise> allCruises = cruiseRepository.findAll();

                // 3. Duyệt danh sách du thuyền và tính toán trạng thái khả dụng
                return allCruises.stream().map(cruise -> {

                        // Trường hợp 1: Tàu không ở trạng thái ACTIVE (VD: MAINTENANCE, INACTIVE)
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

                        // Trường hợp 2: Tàu ACTIVE -> Kiểm tra lịch trùng bằng hàm findConflictingTours
                        List<Tour> conflictingTours = findConflictingTours(
                                        cruise.getId(),
                                        tour.getStartDate(),
                                        tour.getEndDate());

                        // Loại trừ chính Tour này khỏi danh sách trùng (nếu có)
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

        /**
         * =====================================================
         * APPROVE TOUR
         * =====================================================
         */
        public TourResponse approveTour(
                        UUID tourId,
                        UUID cruiseId) {

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
                tour.setStatusTrip(TourStatusTrip.APPROVED);

                Tour savedTour = tourRepository.save(tour);

                return TourMapper.toResponse(savedTour);
        }

        /**
         * =====================================================
         * GET TOURS ĐÃ ĐƯỢC DUYỆT
         * =====================================================
         */
        @Transactional(readOnly = true)
        public List<TourResponse> getApprovedTours() {

                return tourRepository
                                .findAllByStatusTripOrderByNameAsc(
                                                TourStatusTrip.APPROVED)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<OperationCruiseLayoutResponse> getCruiseLayout(
                        UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // Tour chưa được gán Cruise
                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has no cruise assigned",
                                        HttpStatus.BAD_REQUEST);
                }

                UUID cruiseId = tour.getCruise().getId();

                List<CruiseDeck> decks = cruiseDeckRepository
                                .findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                                                cruiseId,
                                                CruiseDeckStatus.ACTIVE);

                return decks.stream()
                                .map(deck -> {

                                        List<OperationCruiseAreaResponse> areas = cruiseAreaRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByNameAsc(
                                                                        deck.getId(),
                                                                        CruiseAreaStatus.ACTIVE)
                                                        .stream()
                                                        .map(area -> new OperationCruiseAreaResponse(
                                                                        area.getId(),
                                                                        area.getName(),
                                                                        area.getDescription(),
                                                                        area.getStatus()))
                                                        .toList();

                                        return new OperationCruiseLayoutResponse(
                                                        deck.getId(),
                                                        deck.getDeckNumber(),
                                                        areas);
                                })
                                .toList();
        }
}