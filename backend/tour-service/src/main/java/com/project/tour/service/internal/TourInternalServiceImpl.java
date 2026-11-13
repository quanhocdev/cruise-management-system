package com.project.tour.service.internal;

import com.project.tour.dto.internal.TourBookingInfoResponse;
import com.project.tour.exception.AppException;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.scheduler.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TourInternalServiceImpl implements TourInternalService {

    private final TourRepository tourRepository;

    public TourInternalServiceImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public TourBookingInfoResponse getTourBookingInfo(UUID tourId) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found: " + tourId,
                        HttpStatus.NOT_FOUND));

        return new TourBookingInfoResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getStartDate(),
                tour.getEndDate());
    }
}