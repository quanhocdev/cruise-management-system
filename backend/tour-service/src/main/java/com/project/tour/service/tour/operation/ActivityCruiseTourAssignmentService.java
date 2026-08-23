package com.project.tour.service.tour.operation;

import com.project.common.event.TourAssignedEvent;
import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentRequest;
import com.project.tour.exception.AppException;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourAssignmentService {

        private final TourRepository tourRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public ActivityCruiseTourAssignmentService(
                        TourRepository tourRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        KafkaTemplate<String, Object> kafkaTemplate) {

                this.tourRepository = tourRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.kafkaTemplate = kafkaTemplate;
        }

        /**
         * Operation phân công một CruiseArea cho Activity của Tour.
         * Validate nghiệp vụ tại tour-service -> Bắn Event Kafka để
         * activity-cruise-service tự lưu DB.
         */
        public void assign(ActivityCruiseTourAssignmentRequest request) {

                // 1. Kiểm tra Tour tồn tại
                Tour tour = tourRepository.findById(request.tourId())
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                // 2. Kiểm tra CruiseArea tồn tại
                CruiseArea cruiseArea = cruiseAreaRepository.findById(request.cruiseAreaId())
                                .orElseThrow(() -> new AppException("Cruise area not found", HttpStatus.NOT_FOUND));

                // 3. Validate logic nghiệp vụ (Area & Tour phải cùng thuộc một du thuyền)
                if (cruiseArea.getStatus() == null) {
                        throw new AppException("Cruise area status is invalid", HttpStatus.BAD_REQUEST);
                }

                if (cruiseArea.getCruiseDeck() == null) {
                        throw new AppException("Cruise area is not assigned to a deck", HttpStatus.BAD_REQUEST);
                }

                if (tour.getCruise() == null) {
                        throw new AppException("Tour has not been assigned to a cruise", HttpStatus.BAD_REQUEST);
                }

                if (!tour.getCruise().getId().equals(cruiseArea.getCruiseDeck().getCruise().getId())) {
                        throw new AppException(
                                        "Cruise area does not belong to the cruise assigned to this tour",
                                        HttpStatus.BAD_REQUEST);
                }

                // 4. Bắn Kafka Event với areaType "ACTIVITY" và action "CREATE" vào topic chung
                // "tour-assignment-topic"
                TourAssignedEvent event = new TourAssignedEvent(
                                request.tourId(),
                                request.cruiseAreaId(),
                                "ACTIVITY",
                                "CREATE");

                kafkaTemplate.send("tour-assignment-topic", request.tourId().toString(), event);
        }

        /**
         * Hủy phân công khu vực hoạt động của Tour.
         * Bắn Kafka Event với action "DELETE" để activity-cruise-service tự xóa bản
         * ghi.
         */
        public void deleteAssignment(UUID tourId, UUID cruiseAreaId) {

                if (!tourRepository.existsById(tourId)) {
                        throw new AppException("Tour not found", HttpStatus.NOT_FOUND);
                }

                if (!cruiseAreaRepository.existsById(cruiseAreaId)) {
                        throw new AppException("Cruise area not found", HttpStatus.NOT_FOUND);
                }

                // Bắn Kafka Event với areaType "ACTIVITY" và action "DELETE" vào topic chung
                // "tour-assignment-topic"
                TourAssignedEvent event = new TourAssignedEvent(
                                tourId,
                                cruiseAreaId,
                                "ACTIVITY",
                                "DELETE");

                kafkaTemplate.send("tour-assignment-topic", tourId.toString(), event);
        }
}