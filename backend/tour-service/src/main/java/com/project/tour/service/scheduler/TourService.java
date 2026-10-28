
package com.project.tour.service.scheduler;

import com.project.tour.dto.scheduler.tour.CreateTourRequest;
import com.project.tour.dto.scheduler.CreateTourWithRouteRequest;
import com.project.tour.dto.scheduler.CreateTourWithRouteResponse;
import com.project.tour.dto.scheduler.tour.TourResponse;
import com.project.tour.dto.scheduler.TourRouteResponse;
import com.project.tour.dto.scheduler.tour.UpdateTourRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.scheduler.TourMapper;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.model.operation.TourPackage;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.tour.TourPackageRepository;
import com.project.tour.service.redis.TourRedisService;

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
        private final TourRedisService tourRedisService;
        private final TourPackageRepository tourPackageRepository;
        private final TourRouteService tourRouteService;

        public TourService(
                        TourRepository tourRepository,
                        TourStatusValidator tourStatusValidator,
                        TourRedisService tourRedisService,
                        TourPackageRepository tourPackageRepository,
                        TourRouteService tourRouteService) {

                this.tourRepository = tourRepository;
                this.tourStatusValidator = tourStatusValidator;
                this.tourRedisService = tourRedisService;
                this.tourPackageRepository = tourPackageRepository;
                this.tourRouteService = tourRouteService;
        }

        // CREATE TOUR - GIỮ API CŨ
        public TourResponse createTour(CreateTourRequest request) {

                validateDates(
                                request.startDate(),
                                request.endDate());

                validateCodeNotExists(request.code());

                Tour tour = TourMapper.toEntity(request);

                Tour savedTour = tourRepository.save(tour);

                return TourMapper.toResponse(savedTour);
        }

        // CREATE TOUR WITH INITIAL ROUTE - API MỚI
        public CreateTourWithRouteResponse createTourWithRoute(
                        CreateTourWithRouteRequest request) {

                validateDates(
                                request.startDate(),
                                request.endDate());

                validateCodeNotExists(request.code());

                if (request.departurePortId() == null
                                || request.destinationPortId() == null) {

                        throw new AppException(
                                        "Departure port and destination port are required",
                                        HttpStatus.BAD_REQUEST);
                }

                if (request.departurePortId().equals(request.destinationPortId())) {
                        throw new AppException(
                                        "Departure port and destination port must be different",
                                        HttpStatus.BAD_REQUEST);
                }

                // Tạo Tour trước.
                Tour tour = new Tour();

                tour.setCode(request.code());
                tour.setName(request.name());
                tour.setDescription(request.description());
                tour.setStartDate(request.startDate());
                tour.setEndDate(request.endDate());

                Tour savedTour = tourRepository.save(tour);

                // Tính toán và lưu tuyến đường ban đầu.
                // Nếu tính toán hoặc lưu route thất bại, transaction sẽ rollback Tour.
                TourRouteResponse routeResponse = tourRouteService.createRoute(
                                savedTour.getId(),
                                request.departurePortId(),
                                request.destinationPortId());

                return new CreateTourWithRouteResponse(
                                TourMapper.toResponse(savedTour),
                                routeResponse);
        }

        // GET BY ID
        @Transactional(readOnly = true)
        public TourResponse getTourById(UUID id) {

                Tour tour = findById(id);

                return TourMapper.toResponse(tour);
        }

        // GET BY CODE
        @Transactional(readOnly = true)
        public TourResponse getTourByCode(String code) {

                Tour tour = tourRepository
                                .findByCodeIgnoreCase(code)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return TourMapper.toResponse(tour);
        }

        // GET TOURS
        @Transactional(readOnly = true)
        public List<TourResponse> getTours(
                        UUID cruiseId,
                        TourStatusTrip statusTrip) {

                List<Tour> tours;

                if (cruiseId == null && statusTrip == null) {

                        tours = tourRepository.findAllByOrderByNameAsc();

                } else if (cruiseId != null && statusTrip == null) {

                        tours = tourRepository.findAllByCruise_IdOrderByNameAsc(
                                        cruiseId);

                } else if (cruiseId == null && statusTrip != null) {

                        tours = tourRepository.findAllByStatusTripOrderByNameAsc(
                                        statusTrip);

                } else {

                        tours = tourRepository
                                        .findAllByCruise_IdAndStatusTripOrderByNameAsc(
                                                        cruiseId,
                                                        statusTrip);
                }

                return tours.stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        // UPDATE
        public TourResponse updateTour(
                        UUID id,
                        UpdateTourRequest request) {

                validateDates(
                                request.startDate(),
                                request.endDate());

                Tour tour = findById(id);

                tourStatusValidator.validateCanUpdate(tour);

                if (tourRepository.existsByCodeIgnoreCaseAndIdNot(
                                request.code(),
                                id)) {

                        throw new AppException(
                                        "Tour code already exists",
                                        HttpStatus.CONFLICT);
                }

                TourMapper.updateEntity(tour, request);

                Tour updatedTour = tourRepository.save(tour);

                return TourMapper.toResponse(updatedTour);
        }

        // DELETE
        public void deleteTour(UUID id) {

                Tour tour = findById(id);

                tourStatusValidator.validateCanDelete(tour);

                // Dọn dẹp Redis cho các TourPackage.
                List<TourPackage> packages = tourPackageRepository.findAllByTourId(id);

                for (TourPackage pkg : packages) {
                        tourRedisService.deletePackageAvailableRooms(pkg.getId());
                }

                tourRepository.delete(tour);
        }

        // FIND
        private Tour findById(UUID id) {

                return tourRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));
        }

        // SUBMIT FOR APPROVAL
        public TourResponse submitForApproval(UUID id) {

                Tour tour = findById(id);

                tourStatusValidator.validateCanSubmitForApproval(tour);

                tour.setStatusTrip(TourStatusTrip.APPROVAL_PENDING);

                Tour updatedTour = tourRepository.save(tour);

                return TourMapper.toResponse(updatedTour);
        }

        // VALIDATION
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

        private void validateCodeNotExists(String code) {

                if (tourRepository.existsByCodeIgnoreCase(code)) {

                        throw new AppException(
                                        "Tour code already exists",
                                        HttpStatus.CONFLICT);
                }
        }
}
