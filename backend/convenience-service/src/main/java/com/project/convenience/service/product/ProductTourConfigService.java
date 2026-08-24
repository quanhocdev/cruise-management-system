package com.project.convenience.service.product;

import com.project.convenience.exception.AppException;
import com.project.convenience.dto.product.convenience.ProductTourConfigRequest;
import com.project.convenience.dto.product.convenience.ProductTourResponse;
import com.project.convenience.mapper.ProductTourMapper;
import com.project.convenience.model.Product;
import com.project.convenience.model.ProductTour;
import com.project.convenience.model.enums.ProductStatus;
import com.project.convenience.model.enums.ProductTourStatus;
import com.project.convenience.repository.ProductRepository;
import com.project.convenience.repository.ProductTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProductTourConfigService {

    private final ProductTourRepository productTourRepository;
    private final ProductRepository productRepository;
    private final ProductTourMapper mapper;

    public ProductTourConfigService(
            ProductTourRepository productTourRepository,
            ProductRepository productRepository,
            ProductTourMapper mapper) {

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
     * CONFIGURED
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
        productTour.setStatus(ProductTourStatus.CONFIGURED);

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
     * CONFIGURED
     */
    public ProductTourResponse updateConfig(
            UUID assignmentId,
            ProductTourConfigRequest request) {

        ProductTour productTour = productTourRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(
                        "Product tour assignment not found",
                        HttpStatus.NOT_FOUND));

        if (productTour.getStatus() != ProductTourStatus.CONFIGURED) {
            throw new AppException(
                    "Only CONFIGURED product tour can be updated",
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