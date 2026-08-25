package com.project.tour.service.tour.operation;

import com.project.common.event.ProductTourConfiguredEvent;
import com.project.tour.model.AssignmentProduct;
import com.project.tour.repository.tour.AssignmentProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperationProductTourService {

    private final AssignmentProductRepository assignmentProductRepository;

    public OperationProductTourService(
            AssignmentProductRepository assignmentProductRepository) {
        this.assignmentProductRepository = assignmentProductRepository;
    }

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

        assignment.setProductId(event.productId());
        assignment.setProductName(event.name());
        assignment.setProductDescription(event.description());
        assignment.setPrice(event.price());
        assignment.setQuantity(event.quantity());
        assignment.setImageUrl(event.imageUrl());
        assignment.setStatus(event.status());

        assignment.setProductTourId(event.productTourId());

        assignmentProductRepository.save(assignment);
    }
}