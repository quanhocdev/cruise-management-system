package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourApprovalService {

    private final TourRepository tourRepository;

    public TourApprovalService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    /**
     * GET TOURS CHỜ DUYỆT
     */
    @Transactional(readOnly = true)
    public List<TourResponse> getPendingTours() {
        return tourRepository
                .findAllByStatusTripOrderByNameAsc(TourStatusTrip.APPROVAL_PENDING)
                .stream()
                .map(TourMapper::toResponse)
                .toList();
    }

    /**
     * DUYỆT TOUR (Sau khi đã gán du thuyền)
     */
    public TourResponse approveTour(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

        if (tour.getStatusTrip() != TourStatusTrip.APPROVAL_PENDING) {
            throw new AppException("Tour is not waiting for approval", HttpStatus.BAD_REQUEST);
        }

        if (tour.getCruise() == null) {
            throw new AppException("Please assign a cruise to this tour before approving",
                    HttpStatus.BAD_REQUEST);
        }

        tour.setStatusTrip(TourStatusTrip.APPROVED);
        Tour savedTour = tourRepository.save(tour);

        return TourMapper.toResponse(savedTour);
    }

    /**
     * GET TOURS ĐÃ ĐƯỢC DUYỆT
     */
    @Transactional(readOnly = true)
    public List<TourResponse> getApprovedTours() {
        return tourRepository
                .findAllByStatusTripOrderByNameAsc(TourStatusTrip.APPROVED)
                .stream()
                .map(TourMapper::toResponse)
                .toList();
    }
}