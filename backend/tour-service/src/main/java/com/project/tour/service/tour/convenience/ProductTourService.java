package com.project.tour.service.tour.convenience;

import com.project.tour.dto.tour.convenience.product.ProductTourResponse;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.model.enums.convenience.ProductTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.ProductTourAssignmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductTourService {

    private final ProductTourAssignmentRepository productTourRepository;
    private final ProductTourAssignmentMapper mapper;

    public ProductTourService(
            ProductTourAssignmentRepository productTourRepository,
            ProductTourAssignmentMapper mapper) {

        this.productTourRepository = productTourRepository;
        this.mapper = mapper;
    }

    // =====================================================
    // GET CONFIGURABLE PRODUCTS
    // =====================================================

    /**
     * Lấy các ProductTour mà Convenience có thể cấu hình
     * hoặc chỉnh sửa.
     *
     * Điều kiện:
     *
     * Tour.statusTrip = APPROVED
     *
     * ProductTour.status:
     * - WAITING_CONFIG
     * - NOT_STARTED
     */
    @Transactional(readOnly = true)
    public List<ProductTourResponse> getPendingConfig() {

        return productTourRepository
                .findConfigurable(
                        TourStatusTrip.APPROVED,
                        List.of(
                                ProductTourStatus.WAITING_CONFIG,
                                ProductTourStatus.NOT_STARTED))
                .stream()
                .map(mapper::toProductTourResponse)
                .toList();
    }
}