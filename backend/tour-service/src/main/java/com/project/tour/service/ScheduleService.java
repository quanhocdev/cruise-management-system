package com.project.tour.service;

import com.project.tour.dto.schedule.*;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.*;
import com.project.tour.model.enums.*;
import com.project.tour.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service @Transactional
public class ScheduleService {
    private final ScheduleRepository repository;
    private final TourPackageRepository packageRepository;
    private final CruiseRepository cruiseRepository;

    public ScheduleService(ScheduleRepository repository, TourPackageRepository packageRepository,
                           CruiseRepository cruiseRepository) {
        this.repository = repository;
        this.packageRepository = packageRepository;
        this.cruiseRepository = cruiseRepository;
    }

    public ScheduleResponse create(CreateScheduleRequest request) {
        String code = normalizeCode(request.code());
        if (repository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Schedule code already exists: " + code);
        }
        TourPackage tourPackage = findActivePackage(request.tourPackageId());
        Cruise cruise = findActiveCruise(request.cruiseId());
        validate(tourPackage, cruise, request.startDate(), request.endDate(), request.capacity(), null);

        Schedule schedule = new Schedule();
        schedule.setTourPackage(tourPackage); schedule.setCruise(cruise); schedule.setCode(code);
        schedule.setStartDate(request.startDate()); schedule.setEndDate(request.endDate());
        schedule.setCapacity(request.capacity()); schedule.setStatus(ScheduleStatus.DRAFT);
        return toResponse(repository.save(schedule));
    }

    @Transactional(readOnly = true)
    public ScheduleResponse get(UUID id) { return toResponse(find(id)); }

    @Transactional(readOnly = true)
    public ScheduleResponse getByCode(String code) {
        return toResponse(repository.findByCodeIgnoreCase(normalizeCode(code)).orElseThrow(() ->
            new ResourceNotFoundException("Schedule not found with code: " + normalizeCode(code))));
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAll(ScheduleStatus status, boolean upcomingOnly) {
        List<Schedule> schedules;
        if (status != null) {
            schedules = repository.findAllByStatusOrderByStartDateAsc(status);
            if (upcomingOnly) schedules = schedules.stream()
                .filter(s -> !s.getStartDate().isBefore(LocalDate.now())).toList();
        } else if (upcomingOnly) {
            schedules = repository.findAllByStartDateGreaterThanEqualOrderByStartDateAsc(LocalDate.now());
        } else {
            schedules = repository.findAllByOrderByStartDateAsc();
        }
        return schedules.stream().map(this::toResponse).toList();
    }

    public ScheduleResponse update(UUID id, UpdateScheduleRequest request) {
        Schedule schedule = find(id);
        TourPackage tourPackage = findActivePackage(request.tourPackageId());
        Cruise cruise = findActiveCruise(request.cruiseId());
        validate(tourPackage, cruise, request.startDate(), request.endDate(), request.capacity(), id);
        schedule.setTourPackage(tourPackage); schedule.setCruise(cruise);
        schedule.setStartDate(request.startDate()); schedule.setEndDate(request.endDate());
        schedule.setCapacity(request.capacity()); schedule.setStatus(request.status());
        return toResponse(repository.save(schedule));
    }

    public ScheduleResponse cancel(UUID id) {
        Schedule schedule = find(id); schedule.setStatus(ScheduleStatus.CANCELLED);
        return toResponse(repository.save(schedule));
    }

    private void validate(TourPackage p, Cruise c, LocalDate start, LocalDate end,
                          Integer capacity, UUID excludedId) {
        if (end.isBefore(start)) throw new IllegalArgumentException("End date must not be before start date");
        long duration = ChronoUnit.DAYS.between(start, end) + 1;
        if (duration != p.getNumberOfDays())
            throw new IllegalArgumentException("Schedule duration must match package number of days");
        if (capacity > c.getMaxPassengers())
            throw new IllegalArgumentException("Capacity must not exceed cruise maximum passengers");
        if (repository.hasCruiseDateConflict(c.getId(), start, end, excludedId))
            throw new DuplicateResourceException("Cruise already has another schedule in this date range");
    }

    private TourPackage findActivePackage(UUID id) {
        TourPackage p = packageRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Tour package not found with id: " + id));
        if (p.getStatus() != TourPackageStatus.ACTIVE)
            throw new IllegalArgumentException("Tour package must be active");
        return p;
    }
    private Cruise findActiveCruise(UUID id) {
        Cruise c = cruiseRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Cruise not found with id: " + id));
        if (c.getStatus() != CruiseStatus.ACTIVE)
            throw new IllegalArgumentException("Cruise must be active");
        return c;
    }
    private Schedule find(UUID id) {
        return repository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Schedule not found with id: " + id));
    }
    private String normalizeCode(String code) { return code.trim().toUpperCase(Locale.ROOT); }
    private ScheduleResponse toResponse(Schedule s) {
        return new ScheduleResponse(s.getId(), s.getTourPackage().getId(), s.getTourPackage().getName(),
            s.getCruise().getId(), s.getCruise().getName(), s.getCode(), s.getStartDate(), s.getEndDate(),
            s.getCapacity(), s.getStatus());
    }
}
