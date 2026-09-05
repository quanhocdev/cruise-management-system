package com.project.tour.service.tour;

import com.project.tour.dto.booking.TourOpenBookingRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TourBookingService {

    private final TourRepository tourRepository;

    public TourBookingService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public TourResponse getBookingConfig(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Không tìm thấy tour với ID: " + tourId, HttpStatus.NOT_FOUND));
        return TourMapper.toResponse(tour);
    }

    @Transactional
    public TourResponse openBooking(UUID tourId, TourOpenBookingRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Không tìm thấy tour với ID: " + tourId, HttpStatus.NOT_FOUND));

        if (request.bookingEnd().isBefore(request.bookingStart())) {
            throw new AppException("Thời gian đóng booking phải sau thời gian mở booking", HttpStatus.BAD_REQUEST);
        }

        if (tour.getStatusTrip() != TourStatusTrip.READY && tour.getStatusTrip() != TourStatusTrip.APPROVED) {
            throw new AppException("Tour chưa sẵn sàng để cấu hình mở bán", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        tour.setBookingStart(request.bookingStart());
        tour.setBookingEnd(request.bookingEnd());

        if (!now.isBefore(request.bookingStart()) && now.isBefore(request.bookingEnd())) {
            tour.setStatusBooking(TourBookingStatus.OPEN);
        } else {
            tour.setStatusBooking(TourBookingStatus.WAITING);
        }

        if (tour.getStatusTrip() == TourStatusTrip.APPROVED) {
            tour.setStatusTrip(TourStatusTrip.READY);
        }

        Tour savedTour = tourRepository.save(tour);
        return TourMapper.toResponse(savedTour);
    }

    @Transactional
    public TourResponse updateBookingConfig(UUID tourId, TourOpenBookingRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Không tìm thấy tour với ID: " + tourId, HttpStatus.NOT_FOUND));

        if (tour.getStatusBooking() != TourBookingStatus.NOT_OPEN
                && tour.getStatusBooking() != TourBookingStatus.WAITING) {
            throw new AppException(
                    "Không thể chỉnh sửa thời gian booking vì tour đang ở trạng thái: " + tour.getStatusBooking(),
                    HttpStatus.BAD_REQUEST);
        }

        if (request.bookingEnd().isBefore(request.bookingStart())) {
            throw new AppException("Thời gian đóng booking phải sau thời gian mở booking", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        tour.setBookingStart(request.bookingStart());
        tour.setBookingEnd(request.bookingEnd());

        if (!now.isBefore(request.bookingStart()) && now.isBefore(request.bookingEnd())) {
            tour.setStatusBooking(TourBookingStatus.OPEN);
        } else {
            tour.setStatusBooking(TourBookingStatus.WAITING);
        }

        Tour savedTour = tourRepository.save(tour);
        return TourMapper.toResponse(savedTour);
    }

    @Transactional
    public TourResponse deleteBookingConfig(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Không tìm thấy tour với ID: " + tourId, HttpStatus.NOT_FOUND));

        if (tour.getStatusBooking() != TourBookingStatus.NOT_OPEN
                && tour.getStatusBooking() != TourBookingStatus.WAITING) {
            throw new AppException(
                    "Không thể hủy cấu hình booking vì tour đang ở trạng thái: " + tour.getStatusBooking(),
                    HttpStatus.BAD_REQUEST);
        }

        tour.setBookingStart(null);
        tour.setBookingEnd(null);
        tour.setStatusBooking(TourBookingStatus.NOT_OPEN);

        Tour savedTour = tourRepository.save(tour);
        return TourMapper.toResponse(savedTour);
    }
}