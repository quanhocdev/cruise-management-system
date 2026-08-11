package com.project.tour.service;

import com.project.tour.dto.itinerary.*;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.ItineraryDay;
import com.project.tour.model.Schedule;
import com.project.tour.repository.ItineraryDayRepository;
import com.project.tour.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service @Transactional
public class ItineraryDayService {
    private final ItineraryDayRepository repository;
    private final ScheduleRepository scheduleRepository;
    public ItineraryDayService(ItineraryDayRepository repository, ScheduleRepository scheduleRepository) {
        this.repository = repository; this.scheduleRepository = scheduleRepository;
    }
    public ItineraryDayResponse create(UUID scheduleId, CreateItineraryDayRequest request) {
        Schedule schedule = findSchedule(scheduleId);
        validate(schedule, request.dayNumber(), request.itineraryDate());
        if (repository.existsBySchedule_IdAndDayNumber(scheduleId, request.dayNumber())
            || repository.existsBySchedule_IdAndItineraryDate(scheduleId, request.itineraryDate()))
            throw new DuplicateResourceException("Itinerary day number or date already exists in schedule");
        ItineraryDay day = new ItineraryDay(); day.setSchedule(schedule);
        apply(day, request.dayNumber(), request.itineraryDate(), request.title(), request.description());
        return toResponse(repository.save(day));
    }
    @Transactional(readOnly = true)
    public List<ItineraryDayResponse> getAll(UUID scheduleId) {
        findSchedule(scheduleId);
        return repository.findAllBySchedule_IdOrderByDayNumberAsc(scheduleId).stream().map(this::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public ItineraryDayResponse get(UUID scheduleId, UUID id) { return toResponse(find(scheduleId, id)); }
    public ItineraryDayResponse update(UUID scheduleId, UUID id, UpdateItineraryDayRequest request) {
        ItineraryDay day = find(scheduleId, id);
        validate(day.getSchedule(), request.dayNumber(), request.itineraryDate());
        if (repository.existsBySchedule_IdAndDayNumberAndIdNot(scheduleId, request.dayNumber(), id)
            || repository.existsBySchedule_IdAndItineraryDateAndIdNot(scheduleId, request.itineraryDate(), id))
            throw new DuplicateResourceException("Itinerary day number or date already exists in schedule");
        apply(day, request.dayNumber(), request.itineraryDate(), request.title(), request.description());
        return toResponse(repository.save(day));
    }
    public void delete(UUID scheduleId, UUID id) { repository.delete(find(scheduleId, id)); }
    private void validate(Schedule s, Integer number, java.time.LocalDate date) {
        if (date.isBefore(s.getStartDate()) || date.isAfter(s.getEndDate()))
            throw new IllegalArgumentException("Itinerary date must be within schedule date range");
        long expected = ChronoUnit.DAYS.between(s.getStartDate(), date) + 1;
        if (number != expected)
            throw new IllegalArgumentException("Day number must match itinerary date in schedule");
    }
    private void apply(ItineraryDay d, Integer number, java.time.LocalDate date, String title, String description) {
        d.setDayNumber(number); d.setItineraryDate(date); d.setTitle(title.trim()); d.setDescription(trimToNull(description));
    }
    private Schedule findSchedule(UUID id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
    }
    private ItineraryDay find(UUID scheduleId, UUID id) {
        return repository.findByIdAndSchedule_Id(id, scheduleId).orElseThrow(() ->
            new ResourceNotFoundException("Itinerary day not found with id: " + id));
    }
    private String trimToNull(String value) { if (value == null) return null; String v = value.trim(); return v.isEmpty() ? null : v; }
    private ItineraryDayResponse toResponse(ItineraryDay d) {
        return new ItineraryDayResponse(d.getId(), d.getSchedule().getId(), d.getDayNumber(), d.getItineraryDate(), d.getTitle(), d.getDescription());
    }
}
