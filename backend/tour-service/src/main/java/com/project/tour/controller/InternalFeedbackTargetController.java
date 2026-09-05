package com.project.tour.controller;

import com.project.tour.dto.tour.FeedbackTargetContext;
import com.project.tour.exception.AppException;
import com.project.tour.model.*;
import com.project.tour.model.enums.convenience.*;
import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;
import com.project.tour.model.enums.visit.VisitTourStatus;
import com.project.tour.repository.tour.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/internal/tours/{tourId}/feedback-targets")
public class InternalFeedbackTargetController {
    private final ActivityCruiseTourAssignmentRepository onboardRepository;
    private final VisitTourRepository shoreRepository;
    private final ProductTourAssignmentRepository productRepository;
    private final ServiceTourAssignmentRepository serviceRepository;
    private final byte[] expectedApiKey;

    public InternalFeedbackTargetController(ActivityCruiseTourAssignmentRepository onboardRepository,
            VisitTourRepository shoreRepository, ProductTourAssignmentRepository productRepository,
            ServiceTourAssignmentRepository serviceRepository, @Value("${internal.api-key}") String apiKey) {
        this.onboardRepository = onboardRepository;
        this.shoreRepository = shoreRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.expectedApiKey = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{targetType}/{targetId}")
    public FeedbackTargetContext context(@PathVariable UUID tourId, @PathVariable String targetType,
            @PathVariable UUID targetId,
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey) {
        authorize(apiKey);
        return switch (targetType) {
            case "ONBOARD_ACTIVITY" -> onboard(tourId, targetId);
            case "SHORE_ACTIVITY" -> shore(tourId, targetId);
            case "PRODUCT" -> product(tourId, targetId);
            case "SERVICE" -> service(tourId, targetId);
            default -> throw new AppException("Unsupported feedback target type: " + targetType, HttpStatus.BAD_REQUEST);
        };
    }

    private FeedbackTargetContext onboard(UUID tourId, UUID targetId) {
        ActivityCruiseTour item = onboardRepository.findById(targetId)
            .orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTour().getId());
        return result(tourId, "ONBOARD_ACTIVITY", targetId, item.getStatus() == ActivityCruiseTourStatus.COMPLETED);
    }

    private FeedbackTargetContext shore(UUID tourId, UUID targetId) {
        VisitTour item = shoreRepository.findById(targetId).orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getScheduleStop().getSchedule().getTour().getId());
        return result(tourId, "SHORE_ACTIVITY", targetId, item.getStatus() == VisitTourStatus.COMPLETED);
    }

    private FeedbackTargetContext product(UUID tourId, UUID targetId) {
        ProductTour item = productRepository.findById(targetId).orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTour().getId());
        return result(tourId, "PRODUCT", targetId, item.getStatus() == ProductTourStatus.COMPLETED);
    }

    private FeedbackTargetContext service(UUID tourId, UUID targetId) {
        ServiceTour item = serviceRepository.findById(targetId).orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTour().getId());
        return result(tourId, "SERVICE", targetId, item.getStatus() == ServiceTourStatus.COMPLETED);
    }

    private FeedbackTargetContext result(UUID tourId, String type, UUID targetId, boolean completed) {
        return new FeedbackTargetContext(tourId, type, targetId, completed);
    }

    private void verifyTour(UUID requested, UUID actual) {
        if (!requested.equals(actual)) throw new AppException("Feedback target does not belong to this tour", HttpStatus.CONFLICT);
    }

    private AppException notFound(UUID id) {
        return new AppException("Feedback target not found: " + id, HttpStatus.NOT_FOUND);
    }

    private void authorize(String suppliedApiKey) {
        byte[] supplied = suppliedApiKey == null ? new byte[0] : suppliedApiKey.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedApiKey, supplied))
            throw new AppException("Invalid internal API key", HttpStatus.UNAUTHORIZED);
    }
}
