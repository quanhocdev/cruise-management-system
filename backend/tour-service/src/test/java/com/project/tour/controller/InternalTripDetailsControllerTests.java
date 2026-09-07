package com.project.tour.controller;

import com.project.tour.controller.passenger.InternalTripDetailsController;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.model.*;
import com.project.tour.model.enums.tour.*;
import com.project.tour.exception.AppException;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalTripDetailsControllerTests {
    final TourRepository tours = mock(TourRepository.class);
    final RoomRepository rooms = mock(RoomRepository.class);
    final com.project.tour.repository.tour.schedule.ScheduleRepository schedules = mock(com.project.tour.repository.tour.schedule.ScheduleRepository.class);
    final InternalTripDetailsController controller = new InternalTripDetailsController(tours, rooms, schedules, "test-key");

    @Test void rejectsMissingOrWrongKey() {
        assertThrows(AppException.class, () -> controller.get(UUID.randomUUID(), null));
        assertThrows(AppException.class, () -> controller.get(UUID.randomUUID(), "wrong"));
        verifyNoInteractions(tours, rooms, schedules);
    }
    @Test void completedTourRemainsReadableWithRoomDetails() {
        UUID id = UUID.randomUUID();
        Tour tour = new Tour(); tour.setId(id); tour.setName("Past trip"); tour.setStatusTrip(TourStatusTrip.COMPLETED);
        Cruise cruise = new Cruise(); cruise.setId(UUID.randomUUID()); cruise.setName("Ocean"); tour.setCruise(cruise);
        CruiseDeck deck = new CruiseDeck(); deck.setDeckNumber(2);
        RoomType type = new RoomType(); type.setName("Deluxe");
        Room room = new Room(); room.setId(UUID.randomUUID()); room.setCode("A201"); room.setCruiseDeck(deck); room.setRoomType(type);
        when(tours.findById(id)).thenReturn(Optional.of(tour));
        when(rooms.findTripRoomsByCruiseId(cruise.getId())).thenReturn(List.of(room));
        Schedule day = new Schedule(); day.setId(UUID.randomUUID()); day.setDayNumber(1);
        day.setRealDay(java.time.LocalDate.of(2026, 11, 22)); day.setName("Embarkation");
        when(schedules.findAllByTour_IdAndStatusOrderByDayNumberAsc(id, com.project.tour.model.enums.ScheduleStatus.ACTIVE))
            .thenReturn(List.of(day));
        var result = controller.get(id, "test-key");
        assertEquals("Past trip", result.tourName());
        assertEquals("Ocean", result.cruiseName());
        assertEquals("A201", result.rooms().get(0).roomCode());
        assertEquals("Embarkation", result.itinerary().get(0).name());
        assertEquals(1, result.itinerary().get(0).dayNumber());
        verify(schedules).findAllByTour_IdAndStatusOrderByDayNumberAsc(id, com.project.tour.model.enums.ScheduleStatus.ACTIVE);
    }
    @Test void noPublishedScheduleReturnsEmptyList() {
        UUID id = UUID.randomUUID(); Tour tour = new Tour(); tour.setId(id);
        when(tours.findById(id)).thenReturn(Optional.of(tour));
        assertTrue(controller.get(id, "test-key").itinerary().isEmpty());
    }
    @Test void missingTourIsRejected() {
        assertThrows(AppException.class, () -> controller.get(UUID.randomUUID(), "test-key"));
    }
}
