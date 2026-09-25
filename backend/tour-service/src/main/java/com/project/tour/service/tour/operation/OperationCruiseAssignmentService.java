package com.project.tour.service.tour.operation;

import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.enums.TourAssignmentType;

import com.project.tour.model.Schedule;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activityvisit.VisitTour;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.model.convenience.service.ServiceTour;

import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.activityvisit.VisitTourRepository;
import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.convenience.ServiceTourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationCruiseAssignmentService {

        private final ProductTourRepository productTourRepository;
        private final ServiceTourRepository serviceTourRepository;
        private final ActivityCruiseTourAssignmentRepository activityCruiseTourRepository;
        private final VisitTourRepository visitTourRepository;

        private final ScheduleRepository scheduleRepository;

        public OperationCruiseAssignmentService(
                        ProductTourRepository productTourRepository,
                        ServiceTourRepository serviceTourRepository,
                        ActivityCruiseTourAssignmentRepository activityCruiseTourRepository,
                        VisitTourRepository visitTourRepository,
                        ScheduleRepository scheduleRepository) {

                this.productTourRepository = productTourRepository;
                this.serviceTourRepository = serviceTourRepository;
                this.activityCruiseTourRepository = activityCruiseTourRepository;
                this.visitTourRepository = visitTourRepository;
                this.scheduleRepository = scheduleRepository;
        }

        public List<TourAssignmentEvent> getAssignments(UUID tourId) {

                List<TourAssignmentEvent> assignments = new ArrayList<>();

                // =====================================================
                // PRODUCT
                // =====================================================

                List<ProductTour> productTours = productTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                for (ProductTour productTour : productTours) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        productTour.getTourId(),
                                                        productTour.getCruiseAreaId(),
                                                        TourAssignmentType.PRODUCT));
                }

                // =====================================================
                // SERVICE
                // =====================================================

                List<ServiceTour> serviceTours = serviceTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                for (ServiceTour serviceTour : serviceTours) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        serviceTour.getTourId(),
                                                        serviceTour.getCruiseAreaId(),
                                                        TourAssignmentType.SERVICE));
                }

                // =====================================================
                // ACTIVITY CRUISE
                // =====================================================

                List<ActivityCruiseTour> activityCruiseTours = activityCruiseTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId);

                for (ActivityCruiseTour activityCruiseTour : activityCruiseTours) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        activityCruiseTour.getTourId(),
                                                        activityCruiseTour.getCruiseAreaId(),
                                                        TourAssignmentType.ACTIVITY_CRUISE));
                }

                // =====================================================
                // ACTIVITY VISIT
                // =====================================================

                List<VisitTour> visitTours = visitTourRepository
                                .findAllByTourIdOrderByStartTimeAsc(tourId);

                for (VisitTour visitTour : visitTours) {

                        assignments.add(
                                        new TourAssignmentEvent(
                                                        visitTour.getTourId(),
                                                        visitTour.getScheduleStopId(),
                                                        TourAssignmentType.ACTIVITY_VISIT));
                }

                return assignments;
        }
}