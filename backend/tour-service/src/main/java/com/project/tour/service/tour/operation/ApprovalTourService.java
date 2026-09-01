package com.project.tour.service.tour.operation;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.TourMasterSyncEvent;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.mapper.tour.TourMasterSyncMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApprovalTourService {

        private static final String TOUR_APPROVED_TOPIC = "tour-approved-topic";
        private static final String TOUR_MASTER_SYNC_TOPIC = "tour-master-sync-topic";

        private final TourRepository tourRepository;
        private final CruiseDeckRepository cruiseDeckRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ScheduleRepository scheduleRepository;
        private final ScheduleStopRepository scheduleStopRepository;
        private final OperationCruiseAssignmentService cruiseAssignmentService;
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public ApprovalTourService(
                        TourRepository tourRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ScheduleRepository scheduleRepository,
                        ScheduleStopRepository scheduleStopRepository,
                        OperationCruiseAssignmentService cruiseAssignmentService,
                        KafkaTemplate<String, Object> kafkaTemplate) {
                this.tourRepository = tourRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.scheduleRepository = scheduleRepository;
                this.scheduleStopRepository = scheduleStopRepository;
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

        // DUYỆT TOUR
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

                // BÊN TRONG approveTour() của ApprovalTourService.java

                // 1. Lấy assignments
                List<TourAssignmentEvent> assignments = cruiseAssignmentService.getAssignments(tourId);

                // Đổi trạng thái tour
                tour.setStatusTrip(TourStatusTrip.APPROVED);
                Tour savedTour = tourRepository.save(tour);

                // Gửi event
                kafkaTemplate.send(TOUR_APPROVED_TOPIC, tourId.toString(), new TourApprovedEvent(tourId, assignments));

                // 2. Thu thập dữ liệu để truyền vào Mapper
                Cruise cruise = tour.getCruise();
                List<CruiseDeck> decks = cruiseDeckRepository.findAllByCruise_IdOrderByDeckNumberAsc(cruise.getId());

                // Gom Area theo Deck ID để mapping nhanh
                Map<UUID, List<CruiseArea>> deckIdToAreasMap = decks.stream()
                                .collect(Collectors.toMap(
                                                CruiseDeck::getId,
                                                deck -> cruiseAreaRepository
                                                                .findAllByCruiseDeck_IdOrderByNameAsc(deck.getId())));

                List<Schedule> schedules = scheduleRepository.findAllByTour_IdOrderByDayNumberAsc(tourId);

                // Gom Stop theo Schedule ID để mapping nhanh
                Map<UUID, List<ScheduleStop>> scheduleIdToStopsMap = schedules.stream()
                                .collect(Collectors.toMap(
                                                Schedule::getId,
                                                schedule -> scheduleStopRepository
                                                                .findAllBySchedule_IdOrderByStopOrderAsc(
                                                                                schedule.getId())));

                // 3. Sử dụng Mapper để tạo Master Event
                TourMasterSyncEvent masterEvent = TourMasterSyncMapper.toEvent(
                                tour, decks, deckIdToAreasMap, schedules, scheduleIdToStopsMap, assignments);

                // 4. Bắn sang Kafka
                kafkaTemplate.send(TOUR_MASTER_SYNC_TOPIC, tourId.toString(), masterEvent);

                return TourMapper.toResponse(savedTour);
        }

        // GET TOURS ĐÃ ĐƯỢC DUYỆT
        @Transactional(readOnly = true)
        public List<TourResponse> getApprovedTours() {

                return tourRepository
                                .findAllByStatusTripOrderByNameAsc(
                                                TourStatusTrip.APPROVED)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }
}