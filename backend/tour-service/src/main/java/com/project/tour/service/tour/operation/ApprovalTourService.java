package com.project.tour.service.tour.operation;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApprovalTourService {

        private static final String TOUR_APPROVED_TOPIC = "tour-approved-topic";

        private final TourRepository tourRepository;

        private final OperationCruiseAssignmentService cruiseAssignmentService;

        private final KafkaTemplate<String, Object> kafkaTemplate;

        public ApprovalTourService(
                        TourRepository tourRepository,
                        OperationCruiseAssignmentService cruiseAssignmentService,
                        KafkaTemplate<String, Object> kafkaTemplate) {

                this.tourRepository = tourRepository;

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