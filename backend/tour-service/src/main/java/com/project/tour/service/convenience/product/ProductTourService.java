package com.project.tour.service.convenience.product;

import com.project.tour.dto.convenience.product.convenience.ProductTourConfigRequest;
import com.project.tour.dto.convenience.product.convenience.ProductTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.convenience.ProductTourMapper;
import com.project.tour.model.convenience.enums.ProductStatus;
import com.project.tour.model.convenience.enums.ProductTourStatus;
import com.project.tour.model.convenience.product.Product;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.repository.convenience.ProductRepository;
import com.project.tour.repository.convenience.ProductTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductTourService {

    private final ProductTourRepository productTourRepository;
    private final ProductRepository productRepository;
    private final ProductTourMapper mapper;

    public ProductTourService(
            ProductTourRepository productTourRepository,
            ProductRepository productRepository,
            ProductTourMapper mapper) {

        this.productTourRepository = productTourRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    // =====================================================
    // CREATE PRODUCT TOUR
    // =====================================================

    /**
     * Tạo ProductTour cho một Tour + Cruise Area.
     *
     * Trạng thái ban đầu:
     * WAITING_CONFIG
     */
    public void createProductTourFromEvent(
            UUID tourId,
            UUID cruiseAreaId) {

        boolean exists = productTourRepository
                .findByTourIdAndCruiseAreaId(
                        tourId,
                        cruiseAreaId)
                .isPresent();

        if (exists) {
            return;
        }

        ProductTour productTour = new ProductTour();

        productTour.setTourId(tourId);
        productTour.setCruiseAreaId(cruiseAreaId);
        productTour.setStatus(
                ProductTourStatus.WAITING_CONFIG);

        productTourRepository.save(productTour);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProductTourResponse> getAllAssignments() {

        return productTourRepository
                .findAll()
                .stream()
                .map(mapper::toProductTourResponse)
                .toList();
    }

    // =====================================================
    // GET PENDING CONFIG
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProductTourResponse> getPendingConfig() {

        return productTourRepository
                .findConfigurable(
                        List.of(
                                ProductTourStatus.WAITING_CONFIG))
                .stream()
                .map(mapper::toProductTourResponse)
                .toList();
    }

    // =====================================================
    // GET BY TOUR
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProductTourResponse> getByTour(
            UUID tourId) {

        return productTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(mapper::toProductTourResponse)
                .toList();
    }

    // =====================================================
    // CONFIGURE
    // =====================================================

    /**
     * Cấu hình ProductTour lần đầu.
     *
     * WAITING_CONFIG
     * ↓
     * WAITING_CONFIG
     *
     * Lưu cấu hình nhưng CHƯA hoàn tất cấu hình.
     */
    public ProductTourResponse configure(
            UUID productTourId,
            ProductTourConfigRequest request) {

        ProductTour productTour = findProductTour(productTourId);

        if (productTour.getStatus() != ProductTourStatus.WAITING_CONFIG) {

            throw new AppException(
                    "Product tour is not waiting for configuration",
                    HttpStatus.BAD_REQUEST);
        }

        Product product = getActiveProduct(
                request.productId());

        validateQuantity(
                product,
                request.quantity());

        productTour.setProduct(product);
        productTour.setQuantity(
                request.quantity());

        /*
         * Không đổi status ở đây.
         *
         * Vẫn là WAITING_CONFIG.
         */
        ProductTour saved = productTourRepository.save(productTour);

        return mapper.toProductTourResponse(saved);
    }

    // =====================================================
    // UPDATE CONFIG
    // =====================================================

    /**
     * Cập nhật ProductTour khi vẫn đang
     * trong giai đoạn WAITING_CONFIG.
     */
    public ProductTourResponse updateConfig(
            UUID productTourId,
            ProductTourConfigRequest request) {

        ProductTour productTour = findProductTour(productTourId);

        if (productTour.getStatus() != ProductTourStatus.WAITING_CONFIG) {

            throw new AppException(
                    "Product tour configuration has already been completed and cannot be modified",
                    HttpStatus.CONFLICT);
        }

        Product product = getActiveProduct(
                request.productId());

        validateQuantity(
                product,
                request.quantity());

        productTour.setProduct(product);
        productTour.setQuantity(
                request.quantity());

        ProductTour saved = productTourRepository.save(productTour);

        return mapper.toProductTourResponse(saved);
    }

    // =====================================================
    // COMPLETE CONFIGURATION
    // =====================================================

    /**
     * Hoàn tất cấu hình toàn bộ ProductTour
     * của một Tour.
     *
     * WAITING_CONFIG
     * ↓
     * CONFIGURED
     */
    public void completeConfiguration(
            UUID tourId) {

        List<ProductTour> productTours = productTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(
                        tourId);

        if (productTours.isEmpty()) {

            throw new AppException(
                    "No product tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        /*
         * Kiểm tra tất cả phải đang WAITING_CONFIG
         * và đã có product + quantity.
         */
        for (ProductTour productTour : productTours) {

            if (productTour.getStatus() != ProductTourStatus.WAITING_CONFIG) {

                throw new AppException(
                        "Product tour configuration has already been completed or is not in a valid state",
                        HttpStatus.CONFLICT);
            }

            if (productTour.getProduct() == null) {

                throw new AppException(
                        "Product configuration is missing",
                        HttpStatus.BAD_REQUEST);
            }

            if (productTour.getQuantity() == null
                    || productTour.getQuantity() <= 0) {

                throw new AppException(
                        "Product quantity is invalid",
                        HttpStatus.BAD_REQUEST);
            }
        }

        /*
         * Tất cả hợp lệ → chuyển sang CONFIGURED.
         */
        for (ProductTour productTour : productTours) {

            productTour.setStatus(
                    ProductTourStatus.CONFIGURED);
        }

        productTourRepository.saveAll(productTours);
    }

    // =====================================================
    // FIND PRODUCT
    // =====================================================

    private Product getActiveProduct(
            UUID productId) {

        Product product = productRepository
                .findById(productId)
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

        if (quantity == null
                || quantity <= 0) {

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

    // =====================================================
    // FIND PRODUCT TOUR
    // =====================================================

    private ProductTour findProductTour(
            UUID productTourId) {

        return productTourRepository
                .findById(productTourId)
                .orElseThrow(() -> new AppException(
                        "Product tour not found",
                        HttpStatus.NOT_FOUND));
    }
}