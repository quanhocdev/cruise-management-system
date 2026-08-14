package com.project.tour.controller;

import com.project.tour.dto.schedule.ScheduleBookingContext;
import com.project.tour.model.Schedule;
import com.project.tour.repository.ScheduleRepository;
import com.project.tour.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/internal/schedules")
public class InternalScheduleController {
    private final ScheduleRepository repository;
    private final byte[] expectedKey;
    public InternalScheduleController(ScheduleRepository repository, @Value("${internal.api-key}") String key) {
        this.repository = repository; this.expectedKey = key.getBytes(StandardCharsets.UTF_8);
    }
    @GetMapping("/{id}/booking-context")
    public ScheduleBookingContext context(@PathVariable UUID id,
        @RequestHeader(value = "X-Internal-Api-Key", required = false) String key) {
        authorize(key);
        Schedule schedule = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
        return new ScheduleBookingContext(schedule.getId(), schedule.getCapacity(), schedule.getStartDate(), schedule.getStatus());
    }
    private void authorize(String key) {
        byte[] actual = key == null ? new byte[0] : key.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedKey, actual))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key");
    }
}
