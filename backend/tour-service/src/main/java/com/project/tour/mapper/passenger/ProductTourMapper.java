package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.product.PassengerProductDTO;
import com.project.tour.model.convenience.product.ProductTour;
import org.springframework.stereotype.Component;

@Component("passengerProductTourMapper")
public class ProductTourMapper {

    public PassengerProductDTO toDTO(ProductTour productTour) {
        if (productTour == null || productTour.getProduct() == null) {
            return null;
        }

        var product = productTour.getProduct();

        return new PassengerProductDTO(
                product.getId(),
                productTour.getId(),
                productTour.getCruiseAreaId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                productTour.getQuantity(),
                product.getImageUrl());
    }
}