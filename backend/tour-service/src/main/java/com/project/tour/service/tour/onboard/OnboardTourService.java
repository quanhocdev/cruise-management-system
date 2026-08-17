package com.project.tour.service.tour.onboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;

@Service
@Transactional
public class OnboardTourService {
    private final TourRepository tourRepository;

    public OnboardTourService(
            TourRepository tourRepository) {

        this.tourRepository = tourRepository;
    }

    /**
     * =====================================================
     * GET TOURS ĐÃ ĐƯỢC DUYỆT
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<TourResponse> getApprovedTours() {

        return tourRepository
                .findAllByStatusTripOrderByNameAsc(
                        TourStatusTrip.APPROVED)
                .stream()
                .map(TourMapper::toResponse)
                .toList();
    }

}
