package com.project.tour.service;

import com.project.tour.dto.itinerary.CreateItineraryDayRequest;
import com.project.tour.model.Schedule;
import com.project.tour.repository.ItineraryDayRepository;
import com.project.tour.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItineraryDayServiceTests {
    @Mock ItineraryDayRepository repository;
    @Mock ScheduleRepository scheduleRepository;

    @Test void createRejectsDayNumberThatDoesNotMatchDate() {
        UUID scheduleId = UUID.randomUUID(); LocalDate start = LocalDate.of(2026, 9, 1);
        Schedule schedule = new Schedule(); schedule.setStartDate(start); schedule.setEndDate(start.plusDays(2));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        ItineraryDayService service = new ItineraryDayService(repository, scheduleRepository);
        assertThrows(IllegalArgumentException.class, () -> service.create(scheduleId,
            new CreateItineraryDayRequest(1, start.plusDays(1), "Day two", null)));
    }
}
