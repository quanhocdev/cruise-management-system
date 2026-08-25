package com.project.convenience.service.product;

import com.project.common.event.ProductTourConfiguredEvent;
import com.project.convenience.model.Product;
import com.project.convenience.model.ProductTour;
import com.project.convenience.model.enums.ProductTourStatus;
import com.project.convenience.repository.ProductTourRepository;

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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductTourConfigurationService(
            ProductTourRepository productTourRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.productTourRepository = productTourRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void completeConfiguration(UUID tourId) {

        List<ProductTour> productTours = productTourRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);

        if (productTours.isEmpty()) {
            throw new IllegalStateException(
                    "No product tour configuration found for tour");
        }

        for (ProductTour productTour : productTours) {

            if (productTour.getStatus() != ProductTourStatus.CONFIGURED) {
                throw new IllegalStateException(
                        "All product tours must be CONFIGURED before completing configuration");
            }

            Product product = productTour.getProduct();

            if (product == null) {
                throw new IllegalStateException(
                        "Product configuration is missing");
            }

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
                    productTour.getId().toString(),
                    event);
        }
    }
}