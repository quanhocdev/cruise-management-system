package com.project.convenience.service.product;

import com.project.common.event.ProductTourConfiguredEvent;
import com.project.convenience.exception.AppException;
import com.project.convenience.model.HistoryProductTour;
import com.project.convenience.model.Product;
import com.project.convenience.model.ProductTour;
import com.project.convenience.model.enums.ProductTourStatus;
import com.project.convenience.repository.HistoryProductTourRepository;
import com.project.convenience.repository.ProductTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductTourConfigurationService {

    private static final String PRODUCT_TOUR_CONFIGURED_TOPIC = "product-tour-configured-topic";

    private final ProductTourRepository productTourRepository;
    private final HistoryProductTourRepository historyProductTourRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductTourConfigurationService(
            ProductTourRepository productTourRepository,
            HistoryProductTourRepository historyProductTourRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.productTourRepository = productTourRepository;
        this.historyProductTourRepository = historyProductTourRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void completeConfiguration(UUID tourId) {

        // =====================================================
        // 1. KIỂM TRA TOUR ĐÃ HOÀN THÀNH CHƯA
        // =====================================================

        if (historyProductTourRepository.existsByTourId(tourId)) {

            throw new AppException(
                    "Product tour configuration has already been completed",
                    HttpStatus.BAD_REQUEST);
        }

        // =====================================================
        // 2. LẤY TẤT CẢ PRODUCT TOUR
        // =====================================================

        List<ProductTour> productTours = productTourRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId);

        if (productTours.isEmpty()) {

            throw new AppException(
                    "No product tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // 3. KIỂM TRA TẤT CẢ ĐÃ CONFIGURED
        // =====================================================

        for (ProductTour productTour : productTours) {

            if (productTour.getStatus() != ProductTourStatus.CONFIGURED) {

                throw new AppException(
                        "All product tours must be CONFIGURED before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }

            if (productTour.getProduct() == null) {

                throw new AppException(
                        "Product configuration is missing",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // =====================================================
        // 4. LƯU HISTORY
        // =====================================================

        HistoryProductTour history = new HistoryProductTour();

        history.setTourId(tourId);

        history.setTotalConfigurations(
                productTours.size());

        historyProductTourRepository.save(history);

        // =====================================================
        // 5. GỬI KAFKA EVENT
        // =====================================================

        for (ProductTour productTour : productTours) {

            Product product = productTour.getProduct();

            ProductTourConfiguredEvent event = new ProductTourConfiguredEvent(
                    productTour.getId(),
                    productTour.getTourId(),
                    productTour.getCruiseAreaId(),

                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),

                    productTour.getQuantity(),

                    product.getImageUrl(),

                    productTour.getStatus().name(),

                    LocalDateTime.now());

            kafkaTemplate.send(
                    PRODUCT_TOUR_CONFIGURED_TOPIC,
                    tourId.toString(),
                    event);
        }
    }
}