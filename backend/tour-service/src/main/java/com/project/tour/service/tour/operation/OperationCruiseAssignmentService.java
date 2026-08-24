package com.project.tour.service.tour.operation;

import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.enums.TourAssignmentType;
import com.project.tour.model.AssignmentActivityCruise;
import com.project.tour.model.AssignmentProduct;
import com.project.tour.model.AssignmentService;
import com.project.tour.repository.tour.AssignmentActivityCruiseRepository;
import com.project.tour.repository.tour.AssignmentProductRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationCruiseAssignmentService {

        private final AssignmentProductRepository assignmentProductRepository;
        private final AssignmentServiceRepository assignmentServiceRepository;
        private final AssignmentActivityCruiseRepository assignmentActivityCruiseRepository;

        public OperationCruiseAssignmentService(
                        AssignmentProductRepository assignmentProductRepository,
                        AssignmentServiceRepository assignmentServiceRepository,
                        AssignmentActivityCruiseRepository assignmentActivityCruiseRepository) {

                this.assignmentProductRepository = assignmentProductRepository;
                this.assignmentServiceRepository = assignmentServiceRepository;
                this.assignmentActivityCruiseRepository = assignmentActivityCruiseRepository;
        }

        /**
         * Lấy toàn bộ phân công của một Tour.
         *
         * Đọc từ 3 bảng:
         * - assignment_product
         * - assignment_service
         * - assignment_activity_cruise
         *
         * Sau đó chuyển thành List<TourAssignmentEvent>.
         *
         * Không gửi Kafka ở đây.
         */
        public List<TourAssignmentEvent> getAssignments(UUID tourId) {

                List<TourAssignmentEvent> assignments = new ArrayList<>();

                // =====================================================
                // PRODUCT
                // =====================================================

                List<AssignmentProduct> productAssignments = assignmentProductRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                for (AssignmentProduct assignment : productAssignments) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        assignment.getTourId(),
                                                        assignment.getCruiseAreaId(),
                                                        TourAssignmentType.PRODUCT));
                }

                // =====================================================
                // SERVICE
                // =====================================================

                List<AssignmentService> serviceAssignments = assignmentServiceRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                for (AssignmentService assignment : serviceAssignments) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        assignment.getTourId(),
                                                        assignment.getCruiseAreaId(),
                                                        TourAssignmentType.SERVICE));
                }

                // =====================================================
                // ACTIVITY CRUISE
                // =====================================================

                List<AssignmentActivityCruise> activityAssignments = assignmentActivityCruiseRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                for (AssignmentActivityCruise assignment : activityAssignments) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        assignment.getTourId(),
                                                        assignment.getCruiseAreaId(),
                                                        TourAssignmentType.ACTIVITY_CRUISE));
                }

                return assignments;
        }
}