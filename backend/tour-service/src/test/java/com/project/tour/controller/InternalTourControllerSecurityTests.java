package com.project.tour.controller;

import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.model.Cruise;
import com.project.tour.model.Schedule;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.*;
import java.util.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalTourController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {"jwt.secret=cruise-management-system-local-secret-key-2026", "internal.api-key=test-internal-key"})
class InternalTourControllerSecurityTests {
    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    @Autowired MockMvc mockMvc;
    @MockitoBean TourRepository tourRepository;
    @MockitoBean ScheduleRepository scheduleRepository;

    @Test void missingInternalKeyIsRejected() throws Exception {
        mockMvc.perform(get("/internal/tours/{id}/booking-context", ID)).andExpect(status().isUnauthorized());
    }

    @Test void returnsCapacityDepartureAndOpenStatus() throws Exception {
        Cruise cruise = new Cruise(); cruise.setMaxPassengers(120);
        Tour tour = new Tour(); tour.setId(ID); tour.setCruise(cruise);
        tour.setStatusBooking(TourBookingStatus.OPEN); tour.setStatusTrip(TourStatusTrip.UPCOMING);
        tour.setBookingStart(LocalDateTime.now().minusDays(1)); tour.setBookingEnd(LocalDateTime.now().plusDays(1));
        Schedule departure = new Schedule(); departure.setRealDay(LocalDate.now().plusDays(10));
        when(tourRepository.findById(ID)).thenReturn(Optional.of(tour));
        when(scheduleRepository.findFirstByTour_IdAndStatusOrderByRealDayAsc(ID, ScheduleStatus.ACTIVE)).thenReturn(Optional.of(departure));
        mockMvc.perform(get("/internal/tours/{id}/booking-context", ID).header("X-Internal-Api-Key", "test-internal-key"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.capacity").value(120)).andExpect(jsonPath("$.status").value("OPEN"));
    }
}
