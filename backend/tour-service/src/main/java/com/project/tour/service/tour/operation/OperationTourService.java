package com.project.tour.service.tour.operation;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
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
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationTourService {

        private static final String TOUR_APPROVED_TOPIC = "tour-approved-topic";

        private final TourRepository tourRepository;
        private final CruiseRepository cruiseRepository;

        private final CruiseAreaRepository cruiseAreaRepository;
        private final CruiseDeckRepository cruiseDeckRepository;
        private final RoomRepository roomRepository;

        private final OperationCruiseAvailabilityService cruiseAvailabilityService;
        private final OperationCruiseAssignmentService cruiseAssignmentService;

        private final KafkaTemplate<String, Object> kafkaTemplate;

        public OperationTourService(
                        TourRepository tourRepository,
                        CruiseRepository cruiseRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        RoomRepository roomRepository,
                        OperationCruiseAvailabilityService cruiseAvailabilityService,
                        OperationCruiseAssignmentService cruiseAssignmentService,
                        KafkaTemplate<String, Object> kafkaTemplate) {

                this.tourRepository = tourRepository;
                this.cruiseRepository = cruiseRepository;

                this.cruiseAreaRepository = cruiseAreaRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.roomRepository = roomRepository;

                this.cruiseAvailabilityService = cruiseAvailabilityService;

                this.cruiseAssignmentService = cruiseAssignmentService;

                this.kafkaTemplate = kafkaTemplate;
        }

        // =========================================================
        // GET TOURS CHỜ DUYỆT
        // =========================================================

        @Transactional(readOnly = true)
        public List<TourResponse> getPendingTours() {

                return tourRepository
                                .findAllByStatusTripOrderByNameAsc(
                                                TourStatusTrip.APPROVAL_PENDING)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        // =========================================================
        // LẤY CRUISE KHẢ DỤNG
        // =========================================================

        @Transactional(readOnly = true)
        public List<CruiseAvailabilityResponse> getAvailableCruises(
                        UUID tourId) {

                return cruiseAvailabilityService
                                .getAvailableCruises(tourId);
        }

        // =========================================================
        // GÁN DU THUYỀN CHO TOUR
        // =========================================================

        public TourResponse assignCruise(
                        UUID tourId,
                        UUID cruiseId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
                        throw new AppException(
                                        "Tour is not waiting for approval",
                                        HttpStatus.BAD_REQUEST);
                }

                if (tour.getCruise() != null) {
                        throw new AppException(
                                        "Tour has already been assigned a cruise. "
                                                        + "Please unassign first before selecting a new one.",
                                        HttpStatus.BAD_REQUEST);
                }

                Cruise cruise = cruiseRepository.findById(cruiseId)
                                .orElseThrow(() -> new AppException(
                                                "Cruise not found",
                                                HttpStatus.NOT_FOUND));

                if (cruise.getStatus() != CruiseStatus.ACTIVE) {
                        throw new AppException(
                                        "Cruise is not active",
                                        HttpStatus.BAD_REQUEST);
                }

                List<Tour> conflictingTours = tourRepository.findConflictingTours(
                                cruiseId,
                                List.of(
                                                TourStatusTrip.APPROVED,
                                                TourStatusTrip.IN_PROGRESS),
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

        // =========================================================
        // DUYỆT TOUR
        // =========================================================

        public TourResponse approveTour(UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
                        throw new AppException(
                                        "Tour is not waiting for approval",
                                        HttpStatus.BAD_REQUEST);
                }

                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Please assign a cruise to this tour before approving",
                                        HttpStatus.BAD_REQUEST);
                }

                // Lấy toàn bộ assignment
                List<TourAssignmentEvent> assignments = cruiseAssignmentService
                                .getAssignments(tourId);

                // Đổi trạng thái
                tour.setStatusTrip(TourStatusTrip.APPROVED);

                Tour savedTour = tourRepository.save(tour);

                // Tạo event
                TourApprovedEvent event = new TourApprovedEvent(
                                tourId,
                                assignments);

                // Gửi Kafka
                kafkaTemplate.send(
                                TOUR_APPROVED_TOPIC,
                                tourId.toString(),
                                event);

                return TourMapper.toResponse(savedTour);
        }

        // =========================================================
        // GET TOURS ĐÃ ĐƯỢC DUYỆT
        // =========================================================

        @Transactional(readOnly = true)
        public List<TourResponse> getApprovedTours() {

                return tourRepository
                                .findAllByStatusTripOrderByNameAsc(
                                                TourStatusTrip.APPROVED)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        // =========================================================
        // LẤY LAYOUT DU THUYỀN
        // =========================================================

        @Transactional(readOnly = true)
        public List<OperationCruiseLayoutResponse> getCruiseLayout(
                        UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

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

                                        List<CruiseArea> areas = cruiseAreaRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByNameAsc(
                                                                        deck.getId(),
                                                                        CruiseAreaStatus.ACTIVE);

                                        List<Room> rooms = roomRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(
                                                                        deck.getId(),
                                                                        RoomStatus.ACTIVE);

                                        return OperationCruiseMapper
                                                        .toLayoutResponse(
                                                                        deck,
                                                                        areas,
                                                                        rooms);
                                })
                                .toList();
        }
}