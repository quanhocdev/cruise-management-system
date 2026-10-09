package com.project.booking.controller.convenience;

import com.project.booking.dto.convenience.service.ServiceUsageManagementResponse;
import com.project.booking.dto.convenience.service.ServiceUsageRequest;
import com.project.booking.dto.convenience.service.ServiceUsageResponse;
import com.project.booking.service.service.ServiceUsageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convenience/service-usages")
public class ServiceUsageController {

    private final ServiceUsageService serviceUsageService;

    public ServiceUsageController(ServiceUsageService serviceUsageService) {
        this.serviceUsageService = serviceUsageService;
    }

    /**
     * Convenience quẹt NFC:
     * - Check-in nếu passenger chưa sử dụng service.
     * - Check-out nếu passenger đang sử dụng service.
     */
    @PostMapping
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<ServiceUsageResponse> scan(
            @Valid @RequestBody ServiceUsageRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceUsageService.scan(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<List<ServiceUsageManagementResponse>> getManagementUsages() {
        return ResponseEntity.ok(
                serviceUsageService.getManagementUsages());
    }

    @GetMapping("/booking-passenger/{bookingPassengerId}")
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<List<ServiceUsageResponse>> getByBookingPassenger(
            @PathVariable Long bookingPassengerId) {

        return ResponseEntity.ok(
                serviceUsageService.getByBookingPassengerId(
                        bookingPassengerId));
    }
}
