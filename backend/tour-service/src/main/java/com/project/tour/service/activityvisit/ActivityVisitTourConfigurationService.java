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

    // =====================================================
    // COMPLETE CONFIGURATION
    // =====================================================

    public void complete(UUID tourId) {

        List<VisitTour> visitTours = visitTourRepository
                .findAllByTourIdOrderByStartTimeAsc(tourId);

        if (visitTours.isEmpty()) {
            throw new AppException(
                    "No visit tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // KIỂM TRA ĐÃ COMPLETE CONFIGURATION CHƯA
        // =====================================================

        boolean alreadyConfigured = visitTours.stream()
                .allMatch(visitTour ->
                        visitTour.getStatus() == VisitTourStatus.CONFIGURED);

        if (alreadyConfigured) {
            throw new AppException(
                    "Visit tour configuration for this tour has already been completed",
                    HttpStatus.CONFLICT);
        }

        // =====================================================
        // KIỂM TRA TẤT CẢ ĐANG WAITING_CONFIG
        // =====================================================

        for (VisitTour visitTour : visitTours) {

            if (visitTour.getStatus() != VisitTourStatus.WAITING_CONFIG) {

                throw new AppException(
                        "All visit tours must be WAITING_CONFIG before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // =====================================================
        // KIỂM TRA DỮ LIỆU CẤU HÌNH
        // =====================================================

        for (VisitTour visitTour : visitTours) {

            if (visitTour.getName() == null
                    || visitTour.getName().isBlank()
                    || visitTour.getStartTime() == null
                    || visitTour.getEndTime() == null
                    || visitTour.getMaxPassengers() == null
                    || visitTour.getPrice() == null) {

                throw new AppException(
                        "All visit tours must be fully configured before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }

            if (!visitTour.getStartTime()
                    .isBefore(visitTour.getEndTime())) {

                throw new AppException(
                        "Visit tour start time must be before end time",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // =====================================================
        // HOÀN THÀNH CẤU HÌNH
        // WAITING_CONFIG → CONFIGURED
        // =====================================================

        for (VisitTour visitTour : visitTours) {

            visitTour.setStatus(
                    VisitTourStatus.CONFIGURED);
        }

        visitTourRepository.saveAll(visitTours);
    }

    // =====================================================
    // CONFIGURATION DETAIL
    // =====================================================

    @Transactional(readOnly = true)
    public List<VisitTourResponse> getConfigurationHistoryDetail(
            UUID tourId) {

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
