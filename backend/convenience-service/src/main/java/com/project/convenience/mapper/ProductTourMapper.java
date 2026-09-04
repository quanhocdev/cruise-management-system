package com.project.convenience.mapper;

import com.project.convenience.dto.product.convenience.ProductTourResponse;
import com.project.convenience.model.Product;
import com.project.convenience.model.ProductTour;
import org.springframework.stereotype.Component;

@Component
public class ProductTourMapper {

        public ProductTourResponse toProductTourResponse(ProductTour entity) {
                if (entity == null) {
                        return null;
                }

                Product product = entity.getProduct();

                return new ProductTourResponse(
                                entity.getId(),

                                // Tour info (Hiện tại lưu UUID, các thông tin text có thể populate sau via
                                // Feign)
                                entity.getTourId(),
                                null, // tourCode
                                null, // tourName

                                // Product info
                                product != null ? product.getId() : null,
                                product != null ? product.getName() : null,
                                product != null ? product.getDescription() : null,
                                product != null ? product.getImageUrl() : null,

                                // Cruise Area info
                                entity.getCruiseAreaId(),
                                null, // cruiseAreaName

                                // Cruise Deck info
                                null, // cruiseDeckId
                                null, // deckNumber

                                // Configuration
                                entity.getQuantity(),
                                entity.getStatus(),

                                entity.getCreatedAt(),
                                entity.getUpdatedAt());
        }
}