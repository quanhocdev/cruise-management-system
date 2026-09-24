package com.project.tour.service.activityvisit;

import com.project.tour.dto.activityvisit.VisitTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.activityvisit.VisitTourMapper;
import com.project.tour.model.activityvisit.VisitTour;
import com.project.tour.model.activityvisit.enums.VisitTourStatus;
import com.project.tour.repository.activityvisit.VisitTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityVisitTourConfigurationService {

    private final VisitTourRepository visitTourRepository;

    public ActivityVisitTourConfigurationService(
            VisitTourRepository visitTourRepository) {

        this.visitTourRepository = visitTourRepository;
    }

    public void complete(UUID tourId) {

        List<VisitTour> visitTours = visitTourRepository
                .findAllByTourIdOrderByStartTimeAsc(tourId);

        if (visitTours.isEmpty()) {
            throw new AppException(
                    "No visit tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // KIỂM TRA TOUR ĐÃ HOÀN THÀNH CHƯA
        // =====================================================

        boolean alreadyCompleted = visitTours.stream()
                .allMatch(visitTour ->
                        visitTour.getStatus() == VisitTourStatus.COMPLETED);

        if (alreadyCompleted) {
            throw new AppException(
                    "Visit tour configuration for this tour has already been completed",
                    HttpStatus.CONFLICT);
        }

        // =====================================================
        // KIỂM TRA TẤT CẢ ĐÃ CONFIGURED
        // =====================================================

        for (VisitTour visitTour : visitTours) {

            if (visitTour.getStatus() != VisitTourStatus.CONFIGURED) {

                throw new AppException(
                        "All visit tours must be CONFIGURED before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // =====================================================
        // ĐÁNH DẤU TẤT CẢ ĐÃ HOÀN THÀNH
        // =====================================================

        for (VisitTour visitTour : visitTours) {
            visitTour.setStatus(VisitTourStatus.COMPLETED);
        }

        visitTourRepository.saveAll(visitTours);
    }

    @Transactional(readOnly = true)
    public List<VisitTourResponse> getConfigurationHistoryDetail(UUID tourId) {

        List<VisitTour> visitTours = visitTourRepository
                .findAllByTourIdOrderByStartTimeAsc(tourId);

        if (visitTours.isEmpty()) {
            throw new AppException(
                    "No visit tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        return visitTours.stream()
                .map(VisitTourMapper::toResponse)
                .toList();
    }
}
