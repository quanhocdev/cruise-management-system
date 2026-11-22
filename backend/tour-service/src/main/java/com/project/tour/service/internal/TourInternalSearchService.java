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
        return tourPackageRepository.searchToursByStatusAndMinimumPrice(
                TourStatusTrip.READY,
                minPrice,
                PageRequest.of(0, limit));
    }
}