package com.project.tour.service;

import com.project.tour.dto.portcall.CreatePortCallRequest;
import com.project.tour.model.ItineraryDay;
import com.project.tour.model.Port;
import com.project.tour.model.enums.PortStatus;
import com.project.tour.repository.ItineraryDayRepository;
import com.project.tour.repository.PortCallRepository;
import com.project.tour.repository.PortRepository;
import com.project.tour.service.port.PortCallService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.*;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortCallServiceTests {
    @Mock
    PortCallRepository repository;
    @Mock
    ItineraryDayRepository dayRepository;
    @Mock
    PortRepository portRepository;

    @Test
    void createRejectsDepartureBeforeArrival() {
        UUID scheduleId = UUID.randomUUID();
        UUID dayId = UUID.randomUUID();
        UUID portId = UUID.randomUUID();
        ItineraryDay day = new ItineraryDay();
        day.setItineraryDate(LocalDate.of(2026, 9, 1));
        Port port = new Port();
        port.setId(portId);
        port.setStatus(PortStatus.ACTIVE);
        when(dayRepository.findByIdAndSchedule_Id(dayId, scheduleId)).thenReturn(Optional.of(day));
        when(portRepository.findById(portId)).thenReturn(Optional.of(port));
        PortCallService service = new PortCallService(repository, dayRepository, portRepository);
        LocalDateTime arrival = LocalDateTime.of(2026, 9, 1, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> service.create(scheduleId, dayId,
                new CreatePortCallRequest(portId, arrival, arrival.minusHours(1), null)));
    }
}
