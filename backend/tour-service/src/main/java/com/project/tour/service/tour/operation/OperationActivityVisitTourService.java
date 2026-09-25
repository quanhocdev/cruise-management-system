package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityVisitResponse;
import com.project.tour.mapper.tour.operation.AssignmentActivityVisitMapper;
import com.project.tour.repository.activityvisit.VisitTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationActivityVisitTourService {

        private final VisitTourRepository visitTourRepository;

        public OperationActivityVisitTourService(
                        VisitTourRepository visitTourRepository) {

                this.visitTourRepository = visitTourRepository;
        }

        // =========================================================
        // GET ALL
        // =========================================================

        @Transactional(readOnly = true)
        public List<AssignmentActivityVisitResponse> getAll() {

                return visitTourRepository
                                .findAllByOrderByCreatedAtDesc()
                                .stream()
                                .map(AssignmentActivityVisitMapper::toResponse)
                                .toList();
        }

        // =========================================================
        // GET BY TOUR
        // =========================================================

        @Transactional(readOnly = true)
        public List<AssignmentActivityVisitResponse> getByTourId(
                        UUID tourId) {

                return visitTourRepository
                                .findAllByTourIdOrderByStartTimeAsc(tourId)
                                .stream()
                                .map(AssignmentActivityVisitMapper::toResponse)
                                .toList();
        }
}