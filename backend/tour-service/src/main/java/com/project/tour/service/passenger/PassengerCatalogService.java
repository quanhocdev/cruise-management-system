package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.*;
import com.project.tour.exception.AppException;
import com.project.tour.model.*;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerCatalogService {
    private static final List<TourStatusTrip> SELLABLE_TRIP_STATUSES =
        List.of(TourStatusTrip.APPROVED, TourStatusTrip.READY);

    private final TourRepository tourRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;

    public PassengerCatalogService(TourRepository tourRepository,
                                   ScheduleRepository scheduleRepository,
                                   RoomRepository roomRepository) {
        this.tourRepository = tourRepository;
        this.scheduleRepository = scheduleRepository;
        this.roomRepository = roomRepository;
    }

    public List<PassengerTourSummaryResponse> getOpenTours() {
        LocalDateTime now = LocalDateTime.now();
        return tourRepository.findOpenForPassenger(
                TourBookingStatus.OPEN, SELLABLE_TRIP_STATUSES, now, now.toLocalDate())
            .stream().map(this::toSummary).toList();
    }

    public PassengerTourDetailResponse getOpenTour(UUID tourId) {
        Tour tour = findOpenTour(tourId);
        List<PassengerItineraryDayResponse> itinerary = scheduleRepository
            .findAllByTour_IdAndStatusOrderByDayNumberAsc(tourId, ScheduleStatus.ACTIVE)
            .stream().map(this::toItineraryDay).toList();
        Cruise cruise = tour.getCruise();
        return new PassengerTourDetailResponse(
            tour.getId(), tour.getCode(), tour.getName(), tour.getDescription(),
            tour.getStartDate(), tour.getEndDate(), tour.getBookingStart(), tour.getBookingEnd(),
            cruise.getId(), cruise.getName(), cruise.getDescription(), cruise.getImageUrl(),
            cruise.getMaxPassengers(), itinerary);
    }

    public List<PassengerDepartureResponse> getDepartures(UUID tourId) {
        Tour tour = findOpenTour(tourId);
        Cruise cruise = tour.getCruise();
        return List.of(new PassengerDepartureResponse(
            tour.getId(), tour.getId(), tour.getCode(), tour.getStartDate(), tour.getEndDate(),
            cruise.getId(), cruise.getName(), cruise.getMaxPassengers(), "OPEN"));
    }

    public PassengerVoyageBookingContext getBookingContext(UUID voyageId) {
        Tour tour = findOpenTour(voyageId);
        return new PassengerVoyageBookingContext(
            tour.getId(), tour.getCruise().getMaxPassengers(), tour.getStartDate(), "OPEN");
    }

    public List<PassengerRoomCatalogResponse> getRoomCatalog(UUID voyageId) {
        Tour tour = findOpenTour(voyageId);
        return roomRepository.findActiveRoomsByCruiseId(tour.getCruise().getId())
            .stream().map(this::toRoom).toList();
    }

    private Tour findOpenTour(UUID id) {
        LocalDateTime now = LocalDateTime.now();
        return tourRepository.findOpenForPassengerById(
                id, TourBookingStatus.OPEN, SELLABLE_TRIP_STATUSES, now, now.toLocalDate())
            .orElseThrow(() -> new AppException("Tour is not open for booking", HttpStatus.NOT_FOUND));
    }

    private PassengerTourSummaryResponse toSummary(Tour tour) {
        Cruise cruise = tour.getCruise();
        return new PassengerTourSummaryResponse(
            tour.getId(), tour.getCode(), tour.getName(), tour.getDescription(),
            tour.getStartDate(), tour.getEndDate(), cruise.getId(), cruise.getName(), cruise.getImageUrl());
    }

    private PassengerItineraryDayResponse toItineraryDay(Schedule schedule) {
        return new PassengerItineraryDayResponse(
            schedule.getId(), schedule.getDayNumber(), schedule.getRealDay(),
            schedule.getName(), schedule.getDescription());
    }

    private PassengerRoomCatalogResponse toRoom(Room room) {
        RoomType type = room.getRoomType();
        return new PassengerRoomCatalogResponse(
            room.getId(), room.getCode(), room.getCruiseDeck().getId(),
            room.getCruiseDeck().getDeckNumber(), type.getId(), type.getName(),
            type.getDescription(),
            type.getPrice() == null ? java.math.BigDecimal.ZERO : type.getPrice(),
            type.getCapacity() == null ? 1 : type.getCapacity());
    }
}
