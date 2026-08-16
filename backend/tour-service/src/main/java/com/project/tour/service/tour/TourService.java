package com.project.tour.service.tour;

import com.project.tour.dto.tour.CreateTourRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.UpdateTourRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourService {

        private final TourRepository tourRepository;

        private final TourStatusValidator tourStatusValidator;

        public TourService(
                        TourRepository tourRepository,
                        TourStatusValidator tourStatusValidator) {

                this.tourRepository = tourRepository;
                this.tourStatusValidator = tourStatusValidator;
        }

        // =====================================================
        // CREATE
        // =====================================================

        public TourResponse createTour(
                        CreateTourRequest request) {

                validateDates(
                                request.startDate(),
                                request.endDate());

                validateCodeNotExists(
                                request.code());

                Tour tour = TourMapper.toEntity(request);

                Tour savedTour = tourRepository.save(tour);

                return TourMapper.toResponse(savedTour);
        }

        // =====================================================
        // GET BY ID
        // =====================================================

        @Transactional(readOnly = true)
        public TourResponse getTourById(
                        UUID id) {

                Tour tour = findById(id);

                return TourMapper.toResponse(tour);
        }

        // =====================================================
        // GET BY CODE
        // =====================================================

        @Transactional(readOnly = true)
        public TourResponse getTourByCode(
                        String code) {

                Tour tour = tourRepository
                                .findByCodeIgnoreCase(code)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return TourMapper.toResponse(tour);
        }

        // =====================================================
        // GET TOURS
        // =====================================================

        @Transactional(readOnly = true)
        public List<TourResponse> getTours(
                        UUID cruiseId,
                        TourStatusTrip statusTrip) {

                List<Tour> tours;

                /*
                 * Không có cruiseId + không có statusTrip
                 * -> lấy tất cả tour.
                 */
                if (cruiseId == null && statusTrip == null) {

                        tours = tourRepository
                                        .findAllByOrderByNameAsc();

                        /*
                         * Có cruiseId + không có statusTrip
                         * -> lấy tour theo cruise.
                         */
                } else if (cruiseId != null && statusTrip == null) {

                        tours = tourRepository
                                        .findAllByCruise_IdOrderByNameAsc(
                                                        cruiseId);

                        /*
                         * Không có cruiseId + có statusTrip
                         * -> lọc theo trạng thái tour.
                         */
                } else if (cruiseId == null && statusTrip != null) {

                        tours = tourRepository
                                        .findAllByStatusTripOrderByNameAsc(
                                                        statusTrip);

                        /*
                         * Có cả cruiseId + statusTrip
                         * -> lọc theo cả hai điều kiện.
                         */
                } else {

                        tours = tourRepository
                                        .findAllByCruise_IdAndStatusTripOrderByNameAsc(
                                                        cruiseId,
                                                        statusTrip);
                }

                return tours
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // UPDATE
        // =====================================================

        public TourResponse updateTour(
                        UUID id,
                        UpdateTourRequest request) {

                validateDates(
                                request.startDate(),
                                request.endDate());

                Tour tour = findById(id);

                // Chỉ DRAFT mới được phép chỉnh sửa
                tourStatusValidator.validateCanUpdate(tour);

                if (tourRepository.existsByCodeIgnoreCaseAndIdNot(
                                request.code(),
                                id)) {

                        throw new AppException(
                                        "Tour code already exists",
                                        HttpStatus.CONFLICT);
                }

                TourMapper.updateEntity(
                                tour,
                                request);

                Tour updatedTour = tourRepository.save(tour);

                return TourMapper.toResponse(updatedTour);
        }

        // =====================================================
        // DELETE
        // =====================================================

        public void deleteTour(
                        UUID id) {

                Tour tour = findById(id);

                // Chỉ DRAFT mới được phép xóa
                tourStatusValidator.validateCanDelete(tour);

                tourRepository.delete(tour);
        }

        // =====================================================
        // FIND
        // =====================================================

        private Tour findById(
                        UUID id) {

                return tourRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));
        }

        // =====================================================
        // SUBMIT FOR APPROVAL
        // =====================================================

        public TourResponse submitForApproval(UUID id) {

                Tour tour = findById(id);

                tourStatusValidator.validateCanSubmitForApproval(tour);

                tour.setStatusTrip(TourStatusTrip.APPROVAL_PENDING);

                Tour updatedTour = tourRepository.save(tour);

                return TourMapper.toResponse(updatedTour);
        }

        // =====================================================
        // VALIDATION
        // =====================================================

        private void validateDates(
                        LocalDate dayStart,
                        LocalDate dayEnd) {

                if (dayStart == null || dayEnd == null) {
                        throw new AppException(
                                        "Start date and end date are required",
                                        HttpStatus.BAD_REQUEST);
                }

                if (dayEnd.isBefore(dayStart)) {
                        throw new AppException(
                                        "End date must be greater than or equal to start date",
                                        HttpStatus.BAD_REQUEST);
                }
        }

        private void validateCodeNotExists(
                        String code) {

                if (tourRepository.existsByCodeIgnoreCase(code)) {

                        throw new AppException(
                                        "Tour code already exists",
                                        HttpStatus.CONFLICT);
                }
        }
}