
package com.project.tour.service.internal;

import com.project.tour.dto.internal.TourPriceSearchResponse;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourPackageRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TourInternalSearchService {

    private final TourPackageRepository tourPackageRepository;

    public TourInternalSearchService(
            TourPackageRepository tourPackageRepository) {
        this.tourPackageRepository = tourPackageRepository;
    }

    @Transactional(readOnly = true)
    public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
            BigDecimal minPrice,
            int limit) {

        validatePrice(minPrice, "Minimum price");

        return tourPackageRepository.searchToursByStatusAndMinimumPrice(
                TourStatusTrip.READY,
                minPrice,
                PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<TourPriceSearchResponse> searchReadyToursByMaximumPrice(
            BigDecimal maxPrice,
            int limit) {

        validatePrice(maxPrice, "Maximum price");

        return tourPackageRepository.searchToursByStatusAndMaximumPrice(
                TourStatusTrip.READY,
                maxPrice,
                PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<TourPriceSearchResponse> searchReadyToursByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int limit) {

        validatePrice(minPrice, "Minimum price");
        validatePrice(maxPrice, "Maximum price");

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Minimum price must not be greater than maximum price");
        }

        return tourPackageRepository.searchToursByStatusAndPriceRange(
                TourStatusTrip.READY,
                minPrice,
                maxPrice,
                PageRequest.of(0, limit));
    }

    private void validatePrice(BigDecimal price, String fieldName) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero");
        }
    }
}
