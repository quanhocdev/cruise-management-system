package com.project.tour.service.tour.operation;

import com.project.common.event.TourAssignedEvent;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.AssignmentService;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceTourAssignmentService {

        // Khai báo hằng số Topic chung dùng cho tất cả các loại assignment
        private static final String TOUR_ASSIGNMENT_TOPIC = "tour-assignment-topic";

        private final AssignmentServiceRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final ServiceTourAssignmentMapper assignmentMapper;
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public ServiceTourAssignmentService(
                        AssignmentServiceRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        ServiceTourAssignmentMapper assignmentMapper,
                        KafkaTemplate<String, Object> kafkaTemplate) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.assignmentMapper = assignmentMapper;
                this.kafkaTemplate = kafkaTemplate;
        }

        /**
         * Operation phân công một khu vực dịch vụ cho Tour (Tạo bản ghi trong bảng
         * assignment_service).
         */
        public ServiceTourAssignmentResponse assign(ServiceTourAssignmentRequest request) {

                // 1. Kiểm tra Tour tồn tại
                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                // 2. Kiểm tra CruiseArea tồn tại
                CruiseArea cruiseArea = cruiseAreaRepository.findById(request.cruiseAreaId())
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));

                // 3. Validate logic nghiệp vụ (Area & Tour phải cùng thuộc một du thuyền)
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

                if (tour.getCruise() == null) {
                        throw new AppException(
                                        "Tour has not been assigned to a cruise",
                                        HttpStatus.BAD_REQUEST);
                }

                if (!tour.getCruise().getId().equals(cruiseArea.getCruiseDeck().getCruise().getId())) {
                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                /*
                 * Chống phân công trùng.
                 * Nếu Tour + CruiseArea đã tồn tại thì giữ nguyên và trả lại assignment hiện
                 * tại.
                 */
                AssignmentService assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(request.tourId(), request.cruiseAreaId())
                                .orElseGet(() -> {

                                        AssignmentService newAssignment = new AssignmentService(
                                                        request.tourId(),
                                                        request.cruiseAreaId());

                                        // Lưu vào Database của tour-service
                                        AssignmentService savedAssignment = assignmentRepository.save(newAssignment);

                                        // Bắn Kafka Event sang TOPIC CHUNG với areaType "SERVICE" và action "CREATE"
                                        TourAssignedEvent event = new TourAssignedEvent(
                                                        request.tourId(),
                                                        request.cruiseAreaId(),
                                                        "SERVICE",
                                                        "CREATE");

                                        kafkaTemplate.send(TOUR_ASSIGNMENT_TOPIC, request.tourId().toString(), event);

                                        return savedAssignment;
                                });

                return assignmentMapper.toResponse(assignment, tour, cruiseArea);
        }

        /**
         * Lấy toàn bộ phân công dịch vụ của một Tour.
         */
        @Transactional(readOnly = true)
        public List<ServiceTourAssignmentResponse> getByTour(UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(assignment -> {
                                        CruiseArea cruiseArea = cruiseAreaRepository
                                                        .findById(assignment.getCruiseAreaId()).orElse(null);
                                        return assignmentMapper.toResponse(assignment, tour, cruiseArea);
                                })
                                .toList();
        }

        /**
         * Xóa phân công dịch vụ theo tourId và cruiseAreaId.
         */
        @Transactional
        public void deleteAssignment(UUID tourId, UUID cruiseAreaId) {

                if (!assignmentRepository.existsByTourIdAndCruiseAreaId(tourId, cruiseAreaId)) {
                        throw new AppException(
                                        "Assignment not found",
                                        HttpStatus.NOT_FOUND);
                }

                // 1. Xóa trong DB của tour-service
                assignmentRepository.deleteByTourIdAndCruiseAreaId(tourId, cruiseAreaId);

                // 2. Bắn Kafka Event sang TOPIC CHUNG với areaType "SERVICE" và action "DELETE"
                TourAssignedEvent event = new TourAssignedEvent(
                                tourId,
                                cruiseAreaId,
                                "SERVICE",
                                "DELETE");

                kafkaTemplate.send(TOUR_ASSIGNMENT_TOPIC, tourId.toString(), event);
        }
}