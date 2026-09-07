package com.project.tour.service.passenger;

import com.project.tour.model.*;
import com.project.tour.repository.tour.*;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassengerTripActivitiesTests {
    final AssignmentActivityCruiseRepository onboard = mock(AssignmentActivityCruiseRepository.class);
    final AssignmentActivityVisitRepository shore = mock(AssignmentActivityVisitRepository.class);
    final CruiseAreaRepository areas = mock(CruiseAreaRepository.class);
    final ScheduleStopRepository stops = mock(ScheduleStopRepository.class);
    final PassengerTripActivities service = new PassengerTripActivities(onboard, shore, areas, stops);
    @Test void filtersUnconfiguredAndUnknownStates() {
        UUID tour = UUID.randomUUID();
        var draft = activity("WAITING_CONFIG"); var unknown = activity("UNKNOWN"); var ready = activity("CONFIGURED");
        when(onboard.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(draft, unknown, ready));
        var result = service.get(tour);
        assertEquals(1, result.size()); assertEquals(ready.getActivityCruiseTourId(), result.get(0).id());
        assertNull(result.get(0).price());
    }
    @Test void mergesGroupsInTimeOrderAndMapsLocations() {
        UUID tour = UUID.randomUUID(), areaId = UUID.randomUUID(), stopId = UUID.randomUUID();
        var a = activity("NOT_STARTED"); a.setCruiseAreaId(areaId); a.setStartTime(LocalDateTime.of(2026,11,22,10,0));
        CruiseArea area = new CruiseArea(); area.setName("Sun deck"); when(areas.findById(areaId)).thenReturn(Optional.of(area));
        var b = new AssignmentActivityVisit(); b.setVisitTourId(UUID.randomUUID()); b.setVisitName("Visit"); b.setStatus("CANCELLED");
        b.setScheduleStopId(stopId); b.setStartTime(a.getStartTime().minusHours(1));
        Port port = new Port(); port.setName("Harbour"); ScheduleStop stop = new ScheduleStop(); stop.setPort(port);
        when(stops.findById(stopId)).thenReturn(Optional.of(stop));
        when(onboard.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(a));
        when(shore.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(b));
        var result = service.get(tour);
        assertEquals("SHORE", result.get(0).type()); assertEquals("Harbour", result.get(0).location());
        assertEquals("CANCELLED", result.get(0).status()); assertEquals("Sun deck", result.get(1).location());
    }
    @Test void emptyAssignmentsAreEmpty() { assertTrue(service.get(UUID.randomUUID()).isEmpty()); }
    private AssignmentActivityCruise activity(String status) {
        var a = new AssignmentActivityCruise(); a.setActivityCruiseTourId(UUID.randomUUID());
        a.setActivityName("Yoga"); a.setStatus(status); return a;
    }
}
