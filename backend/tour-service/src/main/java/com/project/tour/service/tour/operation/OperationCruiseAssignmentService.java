package com.project.tour.service.tour.operation;

import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.enums.TourAssignmentType;
import com.project.tour.model.AssignmentActivityCruise;
import com.project.tour.model.AssignmentProduct;
import com.project.tour.model.AssignmentService;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.repository.tour.AssignmentActivityCruiseRepository;
import com.project.tour.repository.tour.AssignmentProductRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

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

        private final ScheduleRepository scheduleRepository;
        private final ScheduleStopRepository scheduleStopRepository;

        public OperationCruiseAssignmentService(
                        AssignmentProductRepository assignmentProductRepository,
                        AssignmentServiceRepository assignmentServiceRepository,
                        AssignmentActivityCruiseRepository assignmentActivityCruiseRepository,
                        ScheduleRepository scheduleRepository,
                        ScheduleStopRepository scheduleStopRepository) {

                this.assignmentProductRepository = assignmentProductRepository;
                this.assignmentServiceRepository = assignmentServiceRepository;
                this.assignmentActivityCruiseRepository = assignmentActivityCruiseRepository;

                this.scheduleRepository = scheduleRepository;
                this.scheduleStopRepository = scheduleStopRepository;
        }

        /**
         * Lấy toàn bộ assignment của một Tour.
         *
         * PRODUCT / SERVICE / ACTIVITY_CRUISE:
         * - Lấy từ các bảng assignment tương ứng.
         *
         * ACTIVITY_VISIT:
         * - Không có bảng assignment.
         * - Mỗi ScheduleStop của Tour được xem là một target
         * để Activity Visit cấu hình.
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

                // =====================================================
                // ACTIVITY VISIT
                // =====================================================

                List<Schedule> schedules = scheduleRepository
                                .findAllByTour_IdOrderByDayNumberAsc(tourId);

                if (!schedules.isEmpty()) {

                        List<UUID> scheduleIds = schedules.stream()
                                        .map(Schedule::getId)
                                        .toList();

                        List<ScheduleStop> scheduleStops = scheduleStopRepository
                                        .findAllBySchedule_IdInOrderBySchedule_DayNumberAscStopOrderAsc(
                                                        scheduleIds);

                        for (ScheduleStop scheduleStop : scheduleStops) {

                                assignments.add(
                                                new TourAssignmentEvent(
                                                                tourId,
                                                                scheduleStop.getId(),
                                                                TourAssignmentType.ACTIVITY_VISIT));
                        }
                }

                return assignments;
        }
}