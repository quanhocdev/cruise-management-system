package com.project.tour.service;

import com.project.tour.dto.portcall.*;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.*;
import com.project.tour.model.enums.*;
import com.project.tour.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service @Transactional
public class PortCallService {
    private final PortCallRepository repository;
    private final ItineraryDayRepository dayRepository;
    private final PortRepository portRepository;
    public PortCallService(PortCallRepository repository, ItineraryDayRepository dayRepository, PortRepository portRepository) {
        this.repository = repository; this.dayRepository = dayRepository; this.portRepository = portRepository;
    }
    public PortCallResponse create(UUID scheduleId, UUID dayId, CreatePortCallRequest request) {
        ItineraryDay day = findDay(scheduleId, dayId); Port port = findActivePort(request.portId());
        validateTimes(day, request.plannedArrivalTime(), request.plannedDepartureTime(), null, null, request.returnDeadline());
        if (repository.existsByItineraryDay_IdAndPort_Id(dayId, port.getId()))
            throw new DuplicateResourceException("Port already exists in this itinerary day");
        PortCall call = new PortCall(); call.setItineraryDay(day); call.setPort(port);
        call.setPlannedArrivalTime(request.plannedArrivalTime()); call.setPlannedDepartureTime(request.plannedDepartureTime());
        call.setReturnDeadline(request.returnDeadline()); call.setStatus(PortCallStatus.PLANNED);
        return toResponse(repository.save(call));
    }
    @Transactional(readOnly = true)
    public List<PortCallResponse> getAll(UUID scheduleId, UUID dayId) {
        findDay(scheduleId, dayId);
        return repository.findAllByItineraryDay_IdOrderByPlannedArrivalTimeAsc(dayId).stream().map(this::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public PortCallResponse get(UUID scheduleId, UUID dayId, UUID id) {
        findDay(scheduleId, dayId); return toResponse(find(dayId, id));
    }
    public PortCallResponse update(UUID scheduleId, UUID dayId, UUID id, UpdatePortCallRequest request) {
        ItineraryDay day = findDay(scheduleId, dayId); PortCall call = find(dayId, id);
        Port port = findActivePort(request.portId());
        validateTimes(day, request.plannedArrivalTime(), request.plannedDepartureTime(),
            request.actualArrivalTime(), request.actualDepartureTime(), request.returnDeadline());
        if (repository.existsByItineraryDay_IdAndPort_IdAndIdNot(dayId, port.getId(), id))
            throw new DuplicateResourceException("Port already exists in this itinerary day");
        call.setPort(port); call.setPlannedArrivalTime(request.plannedArrivalTime());
        call.setPlannedDepartureTime(request.plannedDepartureTime()); call.setActualArrivalTime(request.actualArrivalTime());
        call.setActualDepartureTime(request.actualDepartureTime()); call.setReturnDeadline(request.returnDeadline());
        call.setStatus(request.status()); return toResponse(repository.save(call));
    }
    public PortCallResponse cancel(UUID scheduleId, UUID dayId, UUID id) {
        findDay(scheduleId, dayId); PortCall call = find(dayId, id); call.setStatus(PortCallStatus.CANCELLED);
        return toResponse(repository.save(call));
    }
    private void validateTimes(ItineraryDay day, LocalDateTime plannedArrival, LocalDateTime plannedDeparture,
                               LocalDateTime actualArrival, LocalDateTime actualDeparture, LocalDateTime deadline) {
        if (!plannedArrival.toLocalDate().equals(day.getItineraryDate()))
            throw new IllegalArgumentException("Planned arrival date must match itinerary date");
        if (plannedDeparture.isBefore(plannedArrival))
            throw new IllegalArgumentException("Planned departure must not be before planned arrival");
        if (deadline != null && (deadline.isBefore(plannedArrival) || deadline.isAfter(plannedDeparture)))
            throw new IllegalArgumentException("Return deadline must be between planned arrival and departure");
        if (actualDeparture != null && actualArrival == null)
            throw new IllegalArgumentException("Actual arrival is required before actual departure");
        if (actualArrival != null && actualDeparture != null && actualDeparture.isBefore(actualArrival))
            throw new IllegalArgumentException("Actual departure must not be before actual arrival");
    }
    private ItineraryDay findDay(UUID scheduleId, UUID dayId) {
        return dayRepository.findByIdAndSchedule_Id(dayId, scheduleId).orElseThrow(() ->
            new ResourceNotFoundException("Itinerary day not found with id: " + dayId));
    }
    private Port findActivePort(UUID id) {
        Port port = portRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Port not found with id: " + id));
        if (port.getStatus() != PortStatus.ACTIVE) throw new IllegalArgumentException("Port must be active");
        return port;
    }
    private PortCall find(UUID dayId, UUID id) {
        return repository.findByIdAndItineraryDay_Id(id, dayId).orElseThrow(() ->
            new ResourceNotFoundException("Port call not found with id: " + id));
    }
    private PortCallResponse toResponse(PortCall c) {
        return new PortCallResponse(c.getId(), c.getItineraryDay().getId(), c.getPort().getId(), c.getPort().getName(),
            c.getPlannedArrivalTime(), c.getActualArrivalTime(), c.getPlannedDepartureTime(), c.getActualDepartureTime(),
            c.getReturnDeadline(), c.getStatus());
    }
}
