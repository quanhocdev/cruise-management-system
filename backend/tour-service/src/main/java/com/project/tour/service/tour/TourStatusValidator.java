package com.project.tour.service.tour;

import com.project.tour.exception.AppException;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TourStatusValidator {

    // =====================================================
    // UPDATE TOUR
    // =====================================================

    public void validateCanUpdate(Tour tour) {

        validateTour(tour);

        if (tour.getStatusTrip() != TourStatusTrip.DRAFT) {

            throw new AppException(
                    "Tour chỉ được chỉnh sửa khi đang ở trạng thái DRAFT",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =====================================================
    // DELETE TOUR
    // =====================================================

    public void validateCanDelete(Tour tour) {

        validateTour(tour);

        if (tour.getStatusTrip() != TourStatusTrip.DRAFT) {

            throw new AppException(
                    "Tour chỉ được xóa khi đang ở trạng thái DRAFT",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =====================================================
    // SUBMIT FOR APPROVAL
    // =====================================================

    public void validateCanSubmitForApproval(Tour tour) {

        validateTour(tour);

        if (tour.getStatusTrip() != TourStatusTrip.DRAFT) {

            throw new AppException(
                    "Chỉ Tour ở trạng thái DRAFT mới được gửi duyệt",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =====================================================
    // MANAGE SCHEDULE
    // =====================================================

    public void validateCanManageSchedule(Tour tour) {

        validateTour(tour);

        TourStatusTrip status = tour.getStatusTrip();

        /*
         * Scheduler được quản lý Schedule trong các trạng thái:
         *
         * DRAFT
         * APPROVAL_PENDING
         * APPROVED
         * IN_PROGRESS
         *
         * Không được quản lý Schedule khi:
         *
         * COMPLETED
         * CANCELLED
         */
        if (status != TourStatusTrip.DRAFT
                && status != TourStatusTrip.APPROVED
                && status != TourStatusTrip.IN_PROGRESS) {

            throw new AppException(
                    "Không thể quản lý lịch trình khi Tour đang chờ duyệt, đã hoàn thành hoặc bị hủy",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =====================================================
    // COMMON VALIDATION
    // =====================================================

    private void validateTour(Tour tour) {

        if (tour == null) {

            throw new AppException(
                    "Tour not found",
                    HttpStatus.NOT_FOUND);
        }
    }
}