package com.project.tour.service.tour.convenience;

import com.project.tour.dto.tour.convenience.product.ProductTourConfigRequest;
import com.project.tour.dto.tour.convenience.product.ProductTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ProductTourAssignmentMapper;
import com.project.tour.model.Product;
import com.project.tour.model.ProductTour;
import com.project.tour.model.enums.ProductStatus;
import com.project.tour.model.enums.convenience.ProductTourStatus;
import com.project.tour.repository.product.ProductRepository;
import com.project.tour.repository.tour.ProductTourAssignmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProductTourConfigService {

    private final ProductTourAssignmentRepository productTourRepository;
    private final ProductRepository productRepository;
    private final ProductTourAssignmentMapper mapper;

    public ProductTourConfigService(
            ProductTourAssignmentRepository productTourRepository,
            ProductRepository productRepository,
            ProductTourAssignmentMapper mapper) {

        this.productTourRepository = productTourRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    // =====================================================
    // POST CONFIG
    // =====================================================

    /**
     * Cấu hình ProductTour lần đầu.
     *
     * WAITING_CONFIG
     * ↓
     * NOT_STARTED
     */
    public ProductTourResponse configure(
            UUID assignmentId,
            ProductTourConfigRequest request) {

        ProductTour productTour = productTourRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(
                        "Product tour assignment not found",
                        HttpStatus.NOT_FOUND));

        if (productTour.getStatus() != ProductTourStatus.WAITING_CONFIG) {
            throw new AppException(
                    "Product tour is not waiting for configuration",
                    HttpStatus.BAD_REQUEST);
        }

        Product product = getActiveProduct(request.productId());

        validateQuantity(product, request.quantity());

        productTour.setProduct(product);
        productTour.setQuantity(request.quantity());
        productTour.setStatus(ProductTourStatus.NOT_STARTED);

        ProductTour saved = productTourRepository.save(productTour);

        return mapper.toProductTourResponse(saved);
    }

    // =====================================================
    // PATCH CONFIG
    // =====================================================

    /**
     * Cập nhật cấu hình ProductTour.
     *
     * Chỉ được sửa khi:
     *
     * NOT_STARTED
     */
    public ProductTourResponse updateConfig(
            UUID assignmentId,
            ProductTourConfigRequest request) {

        ProductTour productTour = productTourRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(
                        "Product tour assignment not found",
                        HttpStatus.NOT_FOUND));

        if (productTour.getStatus() != ProductTourStatus.NOT_STARTED) {
            throw new AppException(
                    "Only NOT_STARTED product tour can be updated",
                    HttpStatus.BAD_REQUEST);
        }

        Product product = getActiveProduct(request.productId());

        validateQuantity(product, request.quantity());

        productTour.setProduct(product);
        productTour.setQuantity(request.quantity());

        ProductTour saved = productTourRepository.save(productTour);

        return mapper.toProductTourResponse(saved);
    }

    // =====================================================
    // FIND PRODUCT
    // =====================================================

    private Product getActiveProduct(UUID productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(
                        "Product not found",
                        HttpStatus.NOT_FOUND));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new AppException(
                    "Product is not active",
                    HttpStatus.BAD_REQUEST);
        }

        return product;
    }

    // =====================================================
    // VALIDATE QUANTITY
    // =====================================================

    private void validateQuantity(
            Product product,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new AppException(
                    "Quantity must be greater than 0",
                    HttpStatus.BAD_REQUEST);
        }

        if (product.getStockQuantity() == null) {
            throw new AppException(
                    "Product stock quantity is invalid",
                    HttpStatus.BAD_REQUEST);
        }

        if (quantity > product.getStockQuantity()) {
            throw new AppException(
                    "Requested quantity exceeds product stock",
                    HttpStatus.BAD_REQUEST);
        }
    }
}