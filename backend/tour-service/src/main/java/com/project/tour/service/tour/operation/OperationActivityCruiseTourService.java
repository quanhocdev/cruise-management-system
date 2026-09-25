package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityCruiseResponse;
import com.project.tour.mapper.tour.operation.AssignmentActivityCruiseMapper;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationActivityCruiseTourService {

        private final ActivityCruiseTourAssignmentRepository assignmentRepository;

        public OperationActivityCruiseTourService(
                        ActivityCruiseTourAssignmentRepository assignmentRepository) {

                this.assignmentRepository = assignmentRepository;
        }

        // GET ALL
        public List<AssignmentActivityCruiseResponse> getAll() {

                return assignmentRepository
                                .findAllByOrderByCreatedAtAsc()
                                .stream()
                                .map(AssignmentActivityCruiseMapper::toResponse)
                                .toList();
        }

        // GET BY TOUR
        public List<AssignmentActivityCruiseResponse> getByTourId(
                        UUID tourId) {

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(AssignmentActivityCruiseMapper::toResponse)
                                .toList();
        }
}
