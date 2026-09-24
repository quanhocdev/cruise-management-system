package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentServiceResponse;
import com.project.tour.mapper.tour.operation.AssignmentServiceMapper;
import com.project.tour.repository.convenience.ServiceTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationServiceTourService {

        private final ServiceTourRepository serviceTourRepository;

        public OperationServiceTourService(
                        ServiceTourRepository serviceTourRepository) {

                this.serviceTourRepository = serviceTourRepository;
        }

        // GET ALL
        public List<AssignmentServiceResponse> getAll() {

                return serviceTourRepository
                                .findAllByOrderByCreatedAtAsc()
                                .stream()
                                .map(AssignmentServiceMapper::toResponse)
                                .toList();
        }

        // GET BY TOUR
        public List<AssignmentServiceResponse> getByTourId(
                        UUID tourId) {

                return serviceTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(AssignmentServiceMapper::toResponse)
                                .toList();
        }
}