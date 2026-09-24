package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentProductResponse;
import com.project.tour.mapper.tour.operation.AssignmentProductMapper;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.repository.convenience.ProductTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OperationProductTourService {

        private final ProductTourRepository productTourRepository;

        public OperationProductTourService(
                        ProductTourRepository productTourRepository) {

                this.productTourRepository = productTourRepository;
        }

        // GET ALL
        public List<AssignmentProductResponse> getAll() {

                return productTourRepository
                                .findAllByOrderByCreatedAtAsc()
                                .stream()
                                .map(AssignmentProductMapper::toResponse)
                                .toList();
        }

        // GET BY TOUR
        public List<AssignmentProductResponse> getProductToursByTourId(
                        UUID tourId) {

                return productTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(AssignmentProductMapper::toResponse)
                                .toList();
        }
}