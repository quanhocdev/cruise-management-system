package com.project.tour.service.tour.operation;

import com.project.tour.dto.cruise.CruiseAvailabilityResponse;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.operation.OperationCruiseLayoutResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.mapper.tour.operation.OperationCruiseMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.RoomStatus;
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
import com.project.tour.repository.room.RoomRepository;
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
        private final RoomRepository roomRepository;

        public OperationTourService(
                        TourRepository tourRepository,
                        CruiseRepository cruiseRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        RoomRepository roomRepository) {
                this.tourRepository = tourRepository;
                this.cruiseRepository = cruiseRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.roomRepository = roomRepository;
        }

        /**
         * GET TOURS CHỜ DUYỆT
         */
        @Transactional(readOnly = true)
        public List<TourResponse> getPendingTours() {
                return tourRepository
                                .findAllByStatusTripOrderByNameAsc(TourStatusTrip.APPROVAL_PENDING)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
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
         * KIỂM TRA CRUISE CÓ BỊ TRÙNG LỊCH KHÔNG
         */
        private List<Tour> findConflictingTours(UUID cruiseId, LocalDate startDate, LocalDate endDate) {
                return tourRepository.findConflictingTours(
                                cruiseId,
                                List.of(TourStatusTrip.APPROVED, TourStatusTrip.IN_PROGRESS),
                                startDate,
                                endDate);
        }

        /**
         * DUYỆT TOUR (Sau khi đã gán du thuyền)
         */
        public TourResponse approveTour(UUID tourId) {
                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
                        throw new AppException("Tour is not waiting for approval", HttpStatus.BAD_REQUEST);
                }

                if (tour.getCruise() == null) {
                        throw new AppException("Please assign a cruise to this tour before approving",
                                        HttpStatus.BAD_REQUEST);
                }

                tour.setStatusTrip(TourStatusTrip.APPROVED);
                Tour savedTour = tourRepository.save(tour);

                return TourMapper.toResponse(savedTour);
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

                // =========================================================================
                // BỔ SUNG: CHẶN ĐỔI TÀU NẾU TOUR ĐÃ ĐƯỢC GÁN DU THUYỀN TRƯỚC ĐÓ
                // =========================================================================
                if (tour.getCruise() != null) {
                        throw new AppException(
                                        "Tour has already been assigned a cruise. Please unassign first before selecting a new one.",
                                        HttpStatus.BAD_REQUEST);
                }
                // =========================================================================

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
         * GET TOURS ĐÃ ĐƯỢC DUYỆT
         */
        @Transactional(readOnly = true)
        public List<TourResponse> getApprovedTours() {
                return tourRepository
                                .findAllByStatusTripOrderByNameAsc(TourStatusTrip.APPROVED)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        /**
         * LẤY LAYOUT DU THUYỀN DÀNH CHO OPERATOR
         */
        @Transactional(readOnly = true)
        public List<OperationCruiseLayoutResponse> getCruiseLayout(UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                if (tour.getCruise() == null) {
                        throw new AppException("Tour has no cruise assigned", HttpStatus.BAD_REQUEST);
                }

                UUID cruiseId = tour.getCruise().getId();

                List<CruiseDeck> decks = cruiseDeckRepository
                                .findAllByCruise_IdAndStatusOrderByDeckNumberAsc(cruiseId, CruiseDeckStatus.ACTIVE);

                return decks.stream()
                                .map(deck -> {
                                        // 1. Lấy danh sách CruiseArea active
                                        List<CruiseArea> areas = cruiseAreaRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByNameAsc(deck.getId(),
                                                                        CruiseAreaStatus.ACTIVE);

                                        // 2. Query thêm danh sách Room thuộc deck này
                                        List<Room> rooms = roomRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(deck.getId(),
                                                                        RoomStatus.ACTIVE); // Thay đổi tên hàm theo
                                                                                            // Repository của bạn

                                        // 3. Truyền đủ 3 tham số vào mapper
                                        return OperationCruiseMapper.toLayoutResponse(deck, areas, rooms);
                                })
                                .toList();
        }
}