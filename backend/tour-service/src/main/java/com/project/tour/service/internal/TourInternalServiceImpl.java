
package com.project.tour.service.internal;

import com.project.tour.dto.internal.TourBookingInfoResponse;
import com.project.tour.dto.scheduler.SchedulerTourDtos.TourDetailResponse;
import com.project.tour.exception.AppException;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.service.scheduler.SchedulerTourService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TourInternalServiceImpl implements TourInternalService {

    private final TourRepository tourRepository;
    private final SchedulerTourService schedulerTourService;

    public TourInternalServiceImpl(TourRepository tourRepository, SchedulerTourService schedulerTourService) {
        this.tourRepository = tourRepository;
        this.schedulerTourService = schedulerTourService;
    }

    @Override
    public TourBookingInfoResponse getTourBookingInfo(UUID tourId) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found: " + tourId,
                        HttpStatus.NOT_FOUND));

        return toResponse(tour);
    }

    @Override
    public List<TourBookingInfoResponse> getToursBookingInfo(List<UUID> tourIds) {

        if (tourIds == null || tourIds.isEmpty()) {
            return List.of();
        }

        // Loại bỏ UUID trùng lặp nhưng vẫn giữ thứ tự ban đầu.
        List<UUID> uniqueTourIds = tourIds.stream()
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (uniqueTourIds.isEmpty()) {
            return List.of();
        }

        Map<UUID, Tour> tourMap = tourRepository.findAllById(uniqueTourIds)
                .stream()
                .collect(Collectors.toMap(Tour::getId, Function.identity()));

        // Nếu có bất kỳ tour nào không tồn tại thì trả về 404.
        List<UUID> missingTourIds = uniqueTourIds.stream()
                .filter(id -> !tourMap.containsKey(id))
                .toList();

        if (!missingTourIds.isEmpty()) {
            throw new AppException(
                    "Tours not found: " + missingTourIds,
                    HttpStatus.NOT_FOUND);
        }

        // Trả kết quả theo đúng thứ tự UUID đầu vào.
        return uniqueTourIds.stream()
                .map(tourMap::get)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TourDetailResponse getTourTrackingDetails(UUID tourId) {
        return schedulerTourService.getTourDetailsWithTracking(tourId);

    }

    private TourBookingInfoResponse toResponse(Tour tour) {
        return new TourBookingInfoResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatusTrip());
    }
}
