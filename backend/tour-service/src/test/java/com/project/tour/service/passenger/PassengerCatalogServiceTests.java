package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.*;
import com.project.tour.model.*;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.model.enums.tour.*;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerCatalogServiceTests {
    @Mock TourRepository tourRepository;
    @Mock ScheduleRepository scheduleRepository;
    @Mock RoomRepository roomRepository;
    PassengerCatalogService service;

    @BeforeEach void setUp() {
        service = new PassengerCatalogService(tourRepository, scheduleRepository, roomRepository);
    }

    @Test void openTourListMapsCruiseInformation() {
        Tour tour = tour();
        when(tourRepository.findOpenForPassenger(eq(TourBookingStatus.OPEN), anyList(), any(), any()))
            .thenReturn(List.of(tour));
        List<PassengerTourSummaryResponse> result = service.getOpenTours();
        assertEquals(1, result.size());
        assertEquals("Ocean Star", result.get(0).cruiseName());
        assertEquals("image.jpg", result.get(0).cruiseImageUrl());
    }

    @Test void detailOnlyIncludesActiveItinerary() {
        Tour tour = tour();
        Schedule schedule = new Schedule();
        schedule.setId(UUID.randomUUID()); schedule.setTour(tour); schedule.setDayNumber(1);
        schedule.setRealDay(tour.getStartDate()); schedule.setName("Embarkation");
        schedule.setStatus(ScheduleStatus.ACTIVE);
        when(tourRepository.findOpenForPassengerById(eq(tour.getId()), eq(TourBookingStatus.OPEN),
            anyList(), any(), any())).thenReturn(Optional.of(tour));
        when(scheduleRepository.findAllByTour_IdAndStatusOrderByDayNumberAsc(tour.getId(), ScheduleStatus.ACTIVE))
            .thenReturn(List.of(schedule));
        PassengerTourDetailResponse result = service.getOpenTour(tour.getId());
        assertEquals(1, result.itinerary().size());
        assertEquals("Embarkation", result.itinerary().get(0).name());
    }

    @Test void bookingContextUsesTourAsVoyage() {
        Tour tour = tour();
        when(tourRepository.findOpenForPassengerById(eq(tour.getId()), eq(TourBookingStatus.OPEN),
            anyList(), any(), any())).thenReturn(Optional.of(tour));
        PassengerVoyageBookingContext result = service.getBookingContext(tour.getId());
        assertEquals(tour.getId(), result.voyageId());
        assertEquals(200, result.capacity());
        assertEquals("OPEN", result.status());
    }

    private Tour tour() {
        Cruise cruise = new Cruise(); cruise.setId(UUID.randomUUID()); cruise.setName("Ocean Star");
        cruise.setCode("OS"); cruise.setMaxPassengers(200); cruise.setImageUrl("image.jpg");
        Tour tour = new Tour(); tour.setId(UUID.randomUUID()); tour.setCode("T001"); tour.setName("Ha Long");
        tour.setDescription("Cruise tour"); tour.setStartDate(LocalDate.now().plusDays(10));
        tour.setEndDate(LocalDate.now().plusDays(12)); tour.setCruise(cruise);
        tour.setStatusTrip(TourStatusTrip.READY); tour.setStatusBooking(TourBookingStatus.OPEN);
        return tour;
    }
}
