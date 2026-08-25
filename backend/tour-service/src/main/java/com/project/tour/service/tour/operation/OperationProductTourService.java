package com.project.tour.service.tour.operation;

import com.project.common.event.ProductTourConfiguredEvent;
import com.project.tour.dto.tour.operation.AssignmentProductResponse;
import com.project.tour.mapper.tour.operation.AssignmentProductMapper;
import com.project.tour.model.AssignmentProduct;
import com.project.tour.repository.tour.AssignmentProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationProductTourService {

    private final AssignmentProductRepository assignmentProductRepository;

    public OperationProductTourService(
            AssignmentProductRepository assignmentProductRepository) {
        this.assignmentProductRepository = assignmentProductRepository;
    }

    // =========================================================
    // KAFKA - PRODUCT TOUR CONFIGURED
    // =========================================================

    public void handleProductTourConfigured(
            ProductTourConfiguredEvent event) {

        AssignmentProduct assignment = assignmentProductRepository
                .findByTourIdAndCruiseAreaId(
                        event.tourId(),
                        event.cruiseAreaId())
                .orElseThrow(() -> new IllegalStateException(
                        "AssignmentProduct not found for tourId="
                                + event.tourId()
                                + ", cruiseAreaId="
                                + event.cruiseAreaId()));

        assignment.setProductTourId(event.productTourId());
        assignment.setProductId(event.productId());
        assignment.setProductName(event.name());
        assignment.setProductDescription(event.description());
        assignment.setPrice(event.price());
        assignment.setQuantity(event.quantity());
        assignment.setImageUrl(event.imageUrl());
        assignment.setStatus(event.status());

        assignmentProductRepository.save(assignment);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional(readOnly = true)
    public List<AssignmentProductResponse> getAll() {

        return assignmentProductRepository
                .findAllByOrderByCreatedAtAsc()
                .stream()
                .map(AssignmentProductMapper::toResponse)
                .toList();
    }

    // =========================================================
    // GET BY TOUR
    // =========================================================

    @Transactional(readOnly = true)
    public List<AssignmentProductResponse> getProductToursByTourId(
            UUID tourId) {

        return assignmentProductRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(AssignmentProductMapper::toResponse)
                .toList();
    }
}