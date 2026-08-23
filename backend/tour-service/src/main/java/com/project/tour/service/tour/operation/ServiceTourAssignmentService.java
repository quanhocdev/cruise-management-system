package com.project.tour.service.tour.operation;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ServiceTourAssignmentMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.ServiceTour;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.convenience.ServiceTourStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.ServiceTourAssignmentRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceTourAssignmentService {

    private final ServiceTourAssignmentRepository assignmentRepository;
    private final TourRepository tourRepository;
    private final CruiseAreaRepository cruiseAreaRepository;
    private final ServiceTourAssignmentMapper assignmentMapper;

    public ServiceTourAssignmentService(
            ServiceTourAssignmentRepository assignmentRepository,
            TourRepository tourRepository,
            CruiseAreaRepository cruiseAreaRepository,
            ServiceTourAssignmentMapper assignmentMapper) {

        this.assignmentRepository = assignmentRepository;
        this.tourRepository = tourRepository;
        this.cruiseAreaRepository = cruiseAreaRepository;
        this.assignmentMapper = assignmentMapper;
    }

    /**
     * Operation phân công một khu vực dịch vụ cho Tour.
     */
    public ServiceTourAssignmentResponse assign(
            ServiceTourAssignmentRequest request) {

        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));

        CruiseArea cruiseArea = cruiseAreaRepository.findById(
                request.cruiseAreaId())
                .orElseThrow(() -> new AppException(
                        "Cruise area not found",
                        HttpStatus.NOT_FOUND));

        if (cruiseArea.getStatus() == null) {
            throw new AppException(
                    "Cruise area status is invalid",
                    HttpStatus.BAD_REQUEST);
        }

        if (cruiseArea.getCruiseDeck() == null) {
            throw new AppException(
                    "Cruise area is not assigned to a deck",
                    HttpStatus.BAD_REQUEST);
        }

        if (tour.getCruise() == null) {
            throw new AppException(
                    "Tour has not been assigned to a cruise",
                    HttpStatus.BAD_REQUEST);
        }

        if (!tour.getCruise()
                .getId()
                .equals(cruiseArea.getCruiseDeck().getCruise().getId())) {

            throw new AppException(
                    "Cruise area does not belong to the cruise assigned to this tour",
                    HttpStatus.BAD_REQUEST);
        }

        /*
         * Chống phân công trùng.
         *
         * Nếu Tour + CruiseArea đã tồn tại thì giữ nguyên
         * và trả lại assignment hiện tại.
         */
        return assignmentRepository
                .findByTourIdAndCruiseAreaId(
                        request.tourId(),
                        request.cruiseAreaId())
                .map(assignmentMapper::toResponse)
                .orElseGet(() -> {

                    ServiceTour assignment = new ServiceTour();

                    assignment.setTour(tour);
                    assignment.setCruiseArea(cruiseArea);

                    /*
                     * Operation chỉ phân công khu vực.
                     *
                     * Convenience sẽ chọn Service sau.
                     */
                    assignment.setService(null);

                    /*
                     * Chưa cấu hình giới hạn.
                     * Convenience sẽ cấu hình sau.
                     */
                    assignment.setMaxPassengers(null);
                    assignment.setDurationMinutes(null);

                    assignment.setStatus(
                            ServiceTourStatus.WAITING_CONFIG);

                    ServiceTour saved = assignmentRepository.save(assignment);

                    return assignmentMapper.toResponse(saved);
                });
    }

    /**
     * Lấy toàn bộ phân công dịch vụ của một Tour.
     */
    @Transactional(readOnly = true)
    public List<ServiceTourAssignmentResponse> getByTour(
            UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new AppException(
                    "Tour not found",
                    HttpStatus.NOT_FOUND);
        }

        return assignmentRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }

    /**
     * Xóa phân công dịch vụ theo tourId và cruiseAreaId.
     *
     * Chỉ được xóa khi Convenience chưa cấu hình dịch vụ.
     */
    public void deleteAssignment(
            UUID tourId,
            UUID cruiseAreaId) {

        ServiceTour assignment = assignmentRepository
                .findByTourIdAndCruiseAreaId(
                        tourId,
                        cruiseAreaId)
                .orElseThrow(() -> new AppException(
                        "Assignment not found",
                        HttpStatus.NOT_FOUND));

        if (assignment.getStatus() != ServiceTourStatus.WAITING_CONFIG) {

            throw new AppException(
                    "Cannot delete an assignment that has already been configured",
                    HttpStatus.BAD_REQUEST);
        }

        assignmentRepository.deleteByTourIdAndCruiseAreaId(
                tourId,
                cruiseAreaId);
    }
}