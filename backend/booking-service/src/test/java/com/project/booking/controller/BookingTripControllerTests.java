package com.project.booking.controller;

import com.project.booking.client.TourClient;
import com.project.booking.config.*;
import com.project.booking.dto.*;
import com.project.booking.service.BookingService;
import com.project.booking.exception.BookingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingTripController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {"jwt.secret=cruise-management-system-local-secret-key-2026", "internal.api-key=test-key"})
class BookingTripControllerTests {
    @Autowired MockMvc mvc;
    @MockitoBean BookingService bookings;
    @MockitoBean TourClient tours;

    @Test void requiresLogin() throws Exception {
        mvc.perform(get("/api/v1/bookings/1/trip-details")).andExpect(status().isUnauthorized());
        verifyNoInteractions(tours);
    }
    @Test void rejectsOtherOwnersBeforeCallingTour() throws Exception {
        when(bookings.get(1L, 7L, false)).thenThrow(new BookingException(HttpStatus.FORBIDDEN, "Not yours"));
        mvc.perform(get("/api/v1/bookings/1/trip-details").with(jwt().jwt(j -> j.claim("userId", 7L))))
            .andExpect(status().isForbidden());
        verifyNoInteractions(tours);
    }
    @Test void returnsOnlyAssignedRooms() throws Exception {
        UUID voyage = UUID.randomUUID(), room = UUID.randomUUID();
        var passenger = new PassengerVoyageResponse(1L, 1L, 7L, "Passenger", null, null, null, null,
            room, null, null, null, null, null, null);
        when(bookings.get(1L, 7L, false)).thenReturn(new BookingResponse(1L, voyage, null, 7L,
            "Contact", "123", null, null, null, null, null, List.of(passenger)));
        when(tours.getTripDetails(voyage)).thenReturn(new BookingTripDetails(voyage, "Tour name", "Cruise",
            null, null, List.of(new BookingTripDetails.RoomDetails(room, "A101", 1, "Deluxe"),
                new BookingTripDetails.RoomDetails(UUID.randomUUID(), "B201", 2, "Suite")),
            List.of(new BookingTripDetails.ItineraryDay(UUID.randomUUID(), 1, java.time.LocalDate.of(2026, 11, 22), "Lên tàu", "Đón khách"))));
        mvc.perform(get("/api/v1/bookings/1/trip-details").with(jwt().jwt(j -> j.claim("userId", 7L))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.tourName").value("Tour name"))
            .andExpect(jsonPath("$.rooms.length()").value(1)).andExpect(jsonPath("$.rooms[0].roomCode").value("A101"))
            .andExpect(jsonPath("$.itinerary[0].dayNumber").value(1))
            .andExpect(jsonPath("$.itinerary[0].name").value("Lên tàu"))
            .andExpect(jsonPath("$.itinerary[0].date").value("2026-11-22"));
    }
}
