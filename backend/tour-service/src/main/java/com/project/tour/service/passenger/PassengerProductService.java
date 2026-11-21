
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.product.PassengerProductDTO;
import com.project.tour.mapper.passenger.ProductTourMapper;
import com.project.tour.model.convenience.enums.ProductTourStatus;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.scheduler.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerProductService {

    private static final Set<ProductTourStatus> PASSENGER_VISIBLE_STATUSES = Set.of(
            ProductTourStatus.CONFIGURED,
            ProductTourStatus.NOT_STARTED,
            ProductTourStatus.IN_PROGRESS,
            ProductTourStatus.OUT_OF_STOCK,
            ProductTourStatus.COMPLETED);

    private final TourRepository tourRepository;
    private final ProductTourRepository productTourRepository;
    private final ProductTourMapper productTourMapper;

    public PassengerProductService(
            TourRepository tourRepository,
            ProductTourRepository productTourRepository,
            ProductTourMapper productTourMapper) {
        this.tourRepository = tourRepository;
        this.productTourRepository = productTourRepository;
        this.productTourMapper = productTourMapper;
    }

    public List<PassengerProductDTO> getProducts(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        List<ProductTour> productTours = productTourRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);

        return productTours.stream()
                .filter(item -> PASSENGER_VISIBLE_STATUSES.contains(item.getStatus()))
                .map(productTourMapper::toDTO)
                .toList();
    }
}
